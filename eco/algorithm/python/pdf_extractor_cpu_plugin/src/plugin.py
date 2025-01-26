# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
Description: PDF抽取
Create: 2024/4/25 10:43
"""
import logging as logger
from typing import List, Dict
import time
from io import BytesIO
import re
import base64

import pdfminer.pdfparser
import pdfplumber
import pdfplumber.page
import cv2 as cv
import pandas as pd
import numpy as np


def pdf_slicer(meta_list: List) -> List[Dict[str, bytes]]:
    pass


class PdfExtractorPlugin:
    def __init__(self):
        self._default_bbox = (0, 0, 0, 0)  # 默认bbox值，可修改
        self._base_scalar = 72  # pdfplumber转换图片时的默认分辨率
        self._scalar = 3  # 默认页面转换为图片时的分辨率倍数，可修改
        self._tolerance = 4  # 去除页眉页尾时的默认的容错率，可修改
        self._header_thresh = 0.25  # 页眉页尾的阈值
        self._overlap_thresh = 0.6  # 计算重叠阈值
        self._en_thresh = 0.8  # 判断英文阈值
        self._sep_thresh = 1.05  # 换行的处理阈值
        self._max_length = 1000  # 最大长度

        self._english_connection_sign = re.compile(r"(?<=[a-zA-Z])-\s")  # 正则匹配英文换行符
        self._content_compile = re.compile(r"(^\d.*\d$)|(\.{7,})")  # 正则匹配目录
        self._en_table_return = re.compile(r"(?<=[0-9a-zA-Z])\n(?=[0-9a-zA-Z])")  # 正则匹配表格中英文前后的换行符

        self._wrap_symbol = ("\uf06c", "·", "•", "", "", "", "", "●", "")
        self._table_settings = {"vertical_strategy": "lines", "horizontal_strategy": "lines"}  # 设置抽取表格参数
        self._extract_text_kwargs = {"x_tolerance_ratio": 0.2}  # 设置抽取文字参数

    @staticmethod
    def _get_bbox(line_text_info: Dict) -> tuple:
        """从文字信息中获取bbox"""
        bbox = (
            line_text_info.get("x0", 0),
            line_text_info.get("top", 0),
            line_text_info.get("x1", 0),
            line_text_info.get("bottom", 0)
        )
        return bbox

    @staticmethod
    def _calculate_bbox_area(bbox: tuple) -> float:
        """计算矩形面积"""
        return (bbox[2] - bbox[0]) * (bbox[3] - bbox[1])

    @staticmethod
    def _get_line_seps(lines: List[Dict]) -> List[float]:
        lines_sep = []
        # 获取两行文字间的距离
        for index, line_info in enumerate(lines):
            if not index:
                continue
            lines_sep.append(line_info.get("bottom", 0) - lines[index - 1].get("top", 0))
        return lines_sep

    @staticmethod
    def _check_multi_column(structure_list: List[Dict], page_height: float) -> bool:
        total_height = sum([paragraph.get("bbox")[3] - paragraph.get("bbox")[1] for paragraph in structure_list])

        return True if round(total_height / page_height) > 1 else False

    @staticmethod
    def _get_max_bbox(pre_bbox: tuple, cur_bbox: tuple) -> tuple:
        max_bbox = (
            min(pre_bbox[0], cur_bbox[0]), min(pre_bbox[1], cur_bbox[1]),
            max(pre_bbox[2], cur_bbox[2]), max(pre_bbox[3], cur_bbox[3])
        )
        return max_bbox

    @staticmethod
    def _compare_horizontal_range(origin_range: tuple, target_range: tuple, threshold: float = 1.02):
        """判断横向是否在同栏中"""
        width_compare = (origin_range[2] - origin_range[0]) * threshold > target_range[2] - target_range[0]
        position_compare = origin_range[2] * threshold > target_range[2]
        return width_compare and position_compare

    @staticmethod
    def _combine_dict_word(word_dicts: List[Dict]) -> str:
        """合并dict中的文字"""
        sorted_structure = sorted(word_dicts, key=lambda x: (x.get("bbox")[1], x.get("bbox")[0]))
        return "\n".join([detail.get("text") for detail in sorted_structure])

    @staticmethod
    def _round_bbox(bbox: tuple) -> tuple:
        """模糊处理bbox"""
        return tuple([pos // 10 * 10 for pos in bbox])

    @staticmethod
    def _open_pdf(data: bytes):
        with pdfplumber.open(BytesIO(data)) as pdf:
            return pdf

    def execute(self, file_meta: dict, data: bytes, model) -> str:
        res = ""
        if int(file_meta.get("totalPageNum", 0)) <= self._max_length:
            total_page_num = file_meta.get("totalPageNum", 0)
        else:
            logger.error("The total pages of PDF is more than %s!", self._max_length)
            total_page_num = self._max_length

        if total_page_num:
            for page_slice in range(0, total_page_num, 10):
                page_range = f"({page_slice},{min(page_slice + 9, total_page_num - 1)})"
                file_meta["pageRange"] = page_range

                logger.info("Start PDF slice! Page range: %s", page_range)
                try:
                    response = pdf_slicer([file_meta])[0].get("data", b"")
                    logger.info("Finish PDF slice!")
                    data = response if isinstance(response, bytes) else base64.b64decode(response)

                    res = self._read_pdf(data, res, model)
                except Exception as e:
                    logger.error("PDF slice %s error: %s", page_range, e)
        else:
            res = self._read_pdf(data, res, model)
        return res.strip()

    def combine_overlap_structure(self, text_line: List[Dict], page_structures: List[Dict]) -> List[Dict]:
        """将重叠的矩形合并"""
        bbox = "bbox"

        # 根据从上到下从左到右进行排序
        text_line.sort(key=lambda x: (x.get("top", 0), x.get("x0", 0)))
        page_structures.sort(key=lambda x: (x.get(bbox, self._default_bbox)[1], x.get(bbox, self._default_bbox)[0]))

        page_structures = self._remove_scalar(page_structures)  # 移除放大的分辨率

        # 计算每一个矩形的重叠面积
        for index, structure in enumerate(page_structures):
            for text_info in text_line:
                # 当模型抽取的版面框的底小于文本框的顶时，跳过当前处理
                if structure.get(bbox, self._default_bbox)[3] < self._get_bbox(text_info)[1]:
                    break

                # 版面框和文本框不重合时，不处理
                if not self._compile_overlap_area(structure.get(bbox, self._default_bbox), self._get_bbox(text_info)):
                    continue

                page_structures[index][bbox] = self._get_max_bbox(structure.get(bbox, self._default_bbox),
                                                                  self._get_bbox(text_info))

        return page_structures

    def _return_symbol(self, sep: float, min_threshold: float, word: str, is_space: str) -> str:
        return "\n" if sep > min_threshold or word.startswith(self._wrap_symbol) else is_space

    def _compile_overlap_area(self, bbox_pre: tuple, bbox_rec: tuple) -> bool:
        """比较两个矩形的重叠面积，如果超过小面积矩形的80%，则返回True，认为两个矩形重复"""
        # 获取第一个矩形的坐标
        x1_pre, y1_pre, x2_pre, y2_pre = bbox_pre

        # 获取第二个矩形的坐标
        x1_rec, y1_rec, x2_rec, y2_rec = bbox_rec

        # 计算重叠区域的左上角坐标
        overlap_x1 = max(x1_pre, x1_rec)
        overlap_y1 = max(y1_pre, y1_rec)

        # 计算重叠区域的右下角坐标
        overlap_x2 = min(x2_pre, x2_rec)
        overlap_y2 = min(y2_pre, y2_rec)

        # 如果宽度和高度有负数，表示没有重叠区域
        if overlap_x2 - overlap_x1 <= 0 or overlap_y2 - overlap_y1 <= 0:
            # 如果没有重叠区域，返回False
            return False

        # 比较重叠部分是否超过小矩形的60%
        return (self._calculate_bbox_area((overlap_x1, overlap_y1, overlap_x2, overlap_y2)) >
                min(self._calculate_bbox_area(bbox_pre), self._calculate_bbox_area(bbox_rec)) * self._overlap_thresh)

    def _remove_scalar(self, page_stt: List[Dict]) -> List[Dict]:
        """去除分辨率放大倍率"""
        for index, structure in enumerate(page_stt):
            page_stt[index]["bbox"] = tuple([pos / self._scalar for pos in structure.get("bbox", self._default_bbox)])
        return page_stt

    def _detect_content(self, page_word):
        """判断这一页是否为为目录页"""
        count_content = 0
        lines = page_word.split("\n")
        for line in lines:
            if re.search(self._content_compile, line):  # 判断单行是否为目录
                count_content += 1
        return count_content >= len(lines) // 2  # 如果超过一半行被判断为目录，则这一页为目录页

    def _page_structure(self, page: pdfplumber.page, model) -> List[Dict]:
        """通过调用模型，获取pdf的版面分析结果"""
        page_image = page.to_image(resolution=self._base_scalar * self._scalar).annotated  # 将pdf页面转换为图像格式
        img = cv.cvtColor(np.asarray(page_image), cv.COLOR_RGB2GRAY)
        return model.layout_engine(img)  # 调用模型，获取版面分析结果

    def _remove_page_header_and_footer(self, page: pdfplumber.page, page_structure: List[Dict]) -> pdfplumber.page:
        """根据版面分析结果，去除页面中的页眉页尾"""
        top = page.bbox[1]
        bottom = page.bbox[3]
        middle = (top + bottom) / 2

        for unit in page_structure:
            header_bottom = max((unit.get("bbox", self._default_bbox)[3]) + self._tolerance, top)
            footer_top = min((unit.get("bbox", self._default_bbox)[1]) - self._tolerance, bottom)

            position_changed = header_bottom == top or footer_top == bottom
            position_threshold = (header_bottom > middle * self._header_thresh and
                                  footer_top < middle * (2 - self._header_thresh))
            if position_changed or position_threshold:
                continue

            if unit.get("type", "") == "header":
                # 进行页面切片，并更新最高点
                page = page.crop(bbox=(0, header_bottom, page.width, bottom), relative=False)
                top = header_bottom
                page_structure.remove(unit)
            elif unit.get("type", "") == "footer":
                # 进行页面切片，并更新最低点
                page = page.crop(bbox=(0, top, page.width, footer_top), relative=False)
                bottom = footer_top
                page_structure.remove(unit)
        return page

    def _get_valid_unit_bbox(self, page_bbox: tuple, unit_bbox: tuple) -> tuple:
        """获取可用的矩形框"""
        bbox = (
            max(unit_bbox[0], page_bbox[0]),
            max(unit_bbox[1], page_bbox[1]),
            min(unit_bbox[2], page_bbox[2]),
            min(unit_bbox[3], page_bbox[3])
        )
        return bbox if bbox[0] < bbox[2] and bbox[1] < bbox[3] else self._default_bbox

    def _extract_table_in_html_form(self, cropped_page: pdfplumber.page) -> str:
        table_list = cropped_page.extract_tables(table_settings=self._table_settings)  # 通过文字的位置进行表格抽取
        res_html = "\n"
        for table in table_list:
            df = pd.DataFrame(data=table)

            # 将表格中完全空白的行和列删除
            df.replace("", np.NaN, inplace=True)
            df.dropna(axis=1, how="all", inplace=True)
            df.dropna(axis=0, how="all", inplace=True)
            df.replace(np.NaN, "", inplace=True)

            df.replace(self._en_table_return, " ", regex=True, inplace=True)  # 将英文前后的换行替换成空格
            df.replace("\n", "", regex=True, inplace=True)  # 删除换行符

            # 将表格从DataFrame格式转换为html格式
            res_html += df.to_html(header=False, index=False).replace('<table border="1" class="dataframe">',
                                                                      '<table>') + "\n"
        return res_html.strip()

    def _word_wrap(self, lines: List[Dict], is_space: str) -> str:
        # 如果版面框中的文字少于两个，则直接返回text内容
        text = "text"
        if len(lines) <= 1:
            return lines[0].get(text, "") if lines else ""

        lines_sep = self._get_line_seps(lines)

        # 设置间距最小阈值，如果段落间距大于最小阈值则进行换行，如果大于则不进行换行
        min_threshold = max(float(np.mean(lines_sep)), 0) * self._sep_thresh

        word_res = lines.pop(0).get(text, "")
        for sep in lines_sep:
            word = lines.pop(0).get(text, "")
            word_res += self._return_symbol(sep, min_threshold, word, is_space)
            word_res += word

        return word_res

    def _extract_word_from_cropped_page(self, whole_page: pdfplumber.page, bbox: tuple, structure_type: str,
                                        is_space: str) -> str:
        cropped_page = whole_page.crop(bbox=bbox, relative=False)

        if structure_type == "table":  # 如果版面类型为表格，则进行表格抽取
            return self._extract_table_in_html_form(cropped_page)
        elif structure_type == "figure":  # 如果版面类型是表格，则直接抽取内容，不做其他处理
            return cropped_page.extract_text(**self._extract_text_kwargs)

        page_word = self._word_wrap(cropped_page.extract_text_lines(**self._extract_text_kwargs), is_space)

        page_word = self._english_connection_sign.sub("", page_word)  # 处理英文连接词

        return page_word.strip()

    def _extract_unrecognized_words(self, page: pdfplumber.page) -> List[Dict]:
        text_list = []
        for text_line in page.extract_text_lines():
            text_list.append({"bbox": self._get_bbox(text_line), "text": text_line.get("text", "")})
        return text_list

    def _sort_horizontal_first(self, structure_list: List[Dict]):
        """横向优先排序"""
        bbox = "bbox"
        structure_key = "structure"
        structure_dicts = []
        for unit in structure_list:
            unit_bbox = unit.get(bbox)
            temp_dict = {bbox: unit_bbox, structure_key: [unit]}

            # 如果是空表格则直接增加
            if not structure_dicts:
                structure_dicts.append(temp_dict)
                continue

            # temp_dict是否使用过
            used_tag = False
            # 判断是否可以和之前的列表在同栏中
            for structure in structure_dicts:
                structure_bbox = structure.get(bbox, self._default_bbox)

                # 判断文字区域是否在同栏
                if self._compare_horizontal_range(structure_bbox, unit_bbox):
                    structure[bbox] = self._get_max_bbox(structure_bbox, unit_bbox)
                    structure[structure_key].append(unit)
                    used_tag = True
                    break

            if not used_tag:
                structure_dicts.append(temp_dict)

        return structure_dicts

    def _sort_multi_column(self, structure_list: List) -> str:
        bbox = "bbox"

        # 根据bbox的y0坐标进行排序
        structure_list.sort(key=lambda x: x.get(bbox, self._default_bbox)[0])

        structure_dicts = self._sort_horizontal_first(structure_list)

        word_dicts = []
        # 将同栏的文字合并
        for structure in structure_dicts:
            word_dicts.append({
                "bbox": self._round_bbox(structure.get(bbox, self._default_bbox)),
                "text": self._combine_dict_word(structure.get("structure", []))
            })

        return self._combine_dict_word(word_dicts) + "\n"

    def _sort_extracted_words(self, structure: Dict, page_height: float) -> str:
        structure_list = []
        for value in structure.values():
            structure_list.extend(value) if value else structure_list

        if self._check_multi_column(structure_list, page_height):
            return self._sort_multi_column(structure_list)

        # 根据bbox的y0和x0坐标进行排序
        structure_list.sort(key=lambda x: (x.get("bbox", self._default_bbox)[1], x.get("bbox", self._default_bbox)[0]))

        unit_text = ""

        for unit in structure_list:
            unit_text += unit.get("text", "") + "\n" if unit.get("text", "") else ""

        return unit_text

    def _extract_words_from_pages(self, page: pdfplumber.page, page_structure: List[Dict]) -> str:
        structure = {}

        # 如果超过80%的内容是英文或数字，则处理换行时将换行符转换为空格
        rough_text = page.extract_text()
        en_rate = sum([i.isascii() for i in rough_text]) / len(rough_text)
        is_space = " " if rough_text and en_rate > self._en_thresh else ""

        for unit in page_structure:
            structure_type = unit.get("type", "")
            if structure_type in ("header", "footer"):  # 如果版面类型是页眉页尾则不进行处理
                continue

            # 获取有效的bbox，bbox不大于page边界值
            unit_bbox = self._get_valid_unit_bbox(page.bbox, unit.get("bbox", self._default_bbox))

            if structure_type not in structure:  # 如果键不存在则创建键值
                structure[structure_type] = []

            if unit_bbox == self._default_bbox:  # 如果是默认框则不处理
                continue

            # 根据文本框对页面切片并抽取文字和表格
            page_text = self._extract_word_from_cropped_page(page, unit_bbox, structure_type, is_space)
            if not page_text:  # 如果文字为空，则跳过
                continue

            structure[structure_type].append({
                "bbox": unit_bbox,
                "text": page_text
            })
            page = page.outside_bbox(unit_bbox, relative=False)  # 将界面中已经抽取文字的位置覆盖掉

        structure["other"] = self._extract_unrecognized_words(page)  # 将未被版面分析抽取的文字抽取出来

        return self._sort_extracted_words(structure, page.height)  # 对文字进行排序

    def _process_pages(self, page: pdfplumber.page, model) -> str:
        """处理页面上的文字"""
        if page.bbox[1] != 0 or page.bbox[0] != 0:  # 如果页面的起始位置不在(0, 0)，则不处理
            logger.error("page %i does not start at position [0, 0] which is unable to be extract", page.page_number,
                         exc_info=True)
            return ""

        # 重新设置默认bbox
        self._default_bbox = (0, 0, page.width, page.height)

        # 通过模型获取页面版面分析结果
        page_structure = self.combine_overlap_structure(page.extract_text_lines(), self._page_structure(page, model))

        # 去除页面中的页眉页尾
        page = self._remove_page_header_and_footer(page, page_structure)

        # 判断目录，如果是目录则进行换行处理
        if self._detect_content(page.extract_text()):
            return page.extract_text(**self._extract_text_kwargs) + "\n"

        # 抽取文字
        return self._extract_words_from_pages(page, sorted(page_structure,
                                                           key=lambda x: x.get("type", "a"), reverse=True))

    def _read_pdf(self, data: bytes, res: str, model) -> str:
        """读取PDF文件流"""
        try:
            pdf = self._open_pdf(data)
            for page in pdf.pages:
                if page.page_number > self._max_length:
                    logger.error("The total pages of PDF is more than %s!", self._max_length)
                    break
                start = time.time()
                res += self._process_pages(page, model)
                logger.info("page %i costs %.6f", page.page_number, time.time() - start)
        except pdfminer.pdfparser.PDFSyntaxError as err:
            logger.error("The PDF is invalid! %s", err, exc_info=True)
        except Exception as err:
            logger.error("PDF process error: %s", err, exc_info=True)
        return res
