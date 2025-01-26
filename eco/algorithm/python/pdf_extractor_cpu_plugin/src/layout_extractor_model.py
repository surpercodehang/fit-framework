# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
since: 2024/4/25 10:25
"""
import gc
from pathlib import Path
import logging

from paddleocr import PPStructure
from paddle.distributed.utils.log_utils import get_logger

get_logger(logging.INFO)


class LayoutExtractorModel:

    def __init__(self):
        resource_path = str(Path(__file__).parent.parent / 'resources')
        model_path = str(Path(resource_path, 'models', 'layout_model_dir'))
        dict_path = str(Path(resource_path, 'dict', 'layout_cdla_dict.txt'))

        # 调用版面抽取模型，不重复抽取表格
        self.layout_engine = PPStructure(
            layout=True,
            recovery=False,
            table=False,
            ocr=False,
            image_orientation=False,
            use_gpu=False,
            layout_dict_path=dict_path,
            layout_model_dir=model_path,
            rec_model_dir=model_path,
            det_model_dir=model_path,
            table_model_dir=model_path,
            layout_score_threshold=0.5
        )

    def __del__(self):
        del self.layout_engine
        gc.collect()
