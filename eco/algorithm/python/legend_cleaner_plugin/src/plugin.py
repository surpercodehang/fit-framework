# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
Description: 图注表注去除
Create: 2023/12/7 15:43
"""
import logging as logger
import re
import time

from typing import List

from common.model import Content


class LegendCleanerPlugin:
    @staticmethod
    def _get_legend_re_compile():
        chinese_legend_prefix = r"(图|表|图片|表格)"
        chinese_legend_number = r"(\d+((\.|-)\d+)*|[a-zA-Z]{1,2}((\.|-)\d+)*)"
        chinese_legend_pattern = r"(?<=\n)" + chinese_legend_prefix + "( )*" + chinese_legend_number + " +.*\n"
        english_legend_pattern = r"(Figure|Table|Fig\.?)"
        english_legend_number = r"(S?\d+((\.|-)\d+)*|[a-zA-Z]{1,2}\d?((\.|-)\d+)*)"
        english_legend_pattern = (r"(?<=\n)" + english_legend_pattern + "( )*"
                                  + english_legend_number + r"(\.|:)? +.*\n")
        legend_re_compile = re.compile('|'.join([chinese_legend_pattern, english_legend_pattern]), re.IGNORECASE)
        return legend_re_compile

    @classmethod
    def _clean_html_tag(cls, input_data: str):
        """移除文档中图注表注等"""
        input_data = ''.join(['\n', input_data, '\n'])
        text = cls._get_legend_re_compile().sub("", input_data)
        return text[1:-1]

    def execute(self, contents: List[Content]) -> List[Content]:
        for content in contents:
            start = time.time()
            content.text = self._clean_html_tag(content.text)
            logger.info("fileName: %s, method: LegendCleanerPlugin costs %.6f s" % (
                content.meta.get("fileName"), time.time() - start
            ))
        return contents
