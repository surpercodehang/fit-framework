# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
since: 2024/4/25 10:25
"""
import logging as logger
import sys
import time
from typing import List
import queue
import threading

from common.model import FlowData
from eco.algorithm.python.pdf_extractor_cpu_plugin.src.plugin import PdfExtractorPlugin
from eco.algorithm.python.pdf_extractor_cpu_plugin.src.layout_extractor_model import LayoutExtractorModel

input_queue = queue.Queue()


class RequestMeta:
    def __init__(self, file_meta, result_queue, file_name, data):
        self.file_meta = file_meta
        self.result_queue: queue.Queue = result_queue
        self.file_name = file_name
        self.data = data


def loop():
    model = None
    while True:
        try:
            meta: RequestMeta = input_queue.get(timeout=10)
        except queue.Empty:
            if model is not None:
                del model
                model = None
                logger.info("method: PDFExtractorCPUPlugin, release model at here")
            continue
        if not model:
            logger.info("method: PDFExtractorCPUPlugin, start init model")
            # 实例化模型，模型版面分析的结构字典为layout_cdla_dict.txt
            model = LayoutExtractorModel()
            logger.info("method: PDFExtractorCPUPlugin, model inited.")
        logger.info("fileName: %s, method: PDFExtractorCPUPlugin, start extract.", meta.file_name)
        try:
            start = time.time()
            res = PdfExtractorPlugin().execute(meta.file_meta, meta.data, model)
            logger.info("fileName: %s, method: PDFExtractorCPUPlugin costs %.6f s",
                        meta.file_name,
                        time.time() - start
                        )
            meta.result_queue.put(res)
        except Exception as e:
            logger.error("fileName: %s, method: PDFExtractorCPUPlugin, ocr extract fail: %s", meta.file_name,
                         e, exc_info=True)
            meta.result_queue.put("")  # 如果处理异常，则返回空字符串


loop_thread = threading.Thread(target=loop)
loop_thread.start()


def pdf_extractor_cpu_plugin(flow_data_list: List[FlowData]):
    contents = [flow_data.passData for flow_data in flow_data_list]
    result_queue = queue.Queue()
    for content in contents:
        start = time.time()
        file_name = content.meta.get("fileName")
        if content.meta.get("totalPageNum"):
            content.data = b""
        logger.info("fileName: %s, total page number: %s", file_name, content.meta.get("totalPageNum"))
        # 如果传入的数据格式不为pdf，则不进行处理
        if content.meta.get("fileType") != "pdf":
            logger.error("fileName: %s, method: PDFExtractorCPUPlugin error! The file is not pdf!",
                         file_name, exc_info=True)
            content.data = b""
            continue
        input_queue.put(RequestMeta(content.meta, result_queue, file_name, content.data))
        content.text = result_queue.get()
        content.meta["fileSize"] = str(sys.getsizeof(content.text))
        content.data = b""
        logger.info("fileName: %s, method: PDFExtractorCPUPlugin costs %.6f s",
                    file_name,
                    time.time() - start
                    )
    return [FlowData(businessData=flow_data.businessData, passData=content, contextData=flow_data.contextData)
            for flow_data, content in zip(flow_data_list, contents)]
