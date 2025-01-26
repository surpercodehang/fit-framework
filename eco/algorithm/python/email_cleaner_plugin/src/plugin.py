# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
Description: 邮件地址匿名化
Create: 2023/12/7 15:43
"""
import logging as logger
import re
import time
from typing import List

from email_validator import validate_email, EmailNotValidError

from common.model import Content


class EmailNumberCleanerPlugin:
    def __init__(self):
        self.front_email_pattern = r'(?<=[^0-9a-zA-Z\!\#\$\%\&\'\*\+\-\/\=\?\^\_\`\{\|\}\~\-])'
        self.back_email_pattern = r'(?=[^0-9a-zA-Z\!\#\$\%\&\'\*\+\-\/\=\?\^\_\`\{\|\}\~\-])'
        self.email_pattern = r'([a-zA-Z\d.\-+_]+\s?@\s?[a-zA-Z\d.\-+_]+\.[a-zA-Z0-9]{2,6})'

    def execute(self, contents: List[Content]):
        for content in contents:
            start = time.time()
            content.text = self._email_number_filter(content.text)
            logger.info("fileName: %s, method: EmailCleanerPlugin costs %.6f s" % (
                content.meta.get("fileName"), time.time() - start
            ))
        return contents

    def _email_number_filter(self, input_data: str):
        """ 邮箱匿名化"""
        mixed_data = ''.join(['龥', input_data, '龥'])
        paired_emails = re.compile(self.front_email_pattern + self.email_pattern + self.back_email_pattern).findall(
            mixed_data)
        if paired_emails:
            for email in paired_emails:
                try:
                    # 验证电子邮件地址
                    validate_email(email, check_deliverability=False)
                    mixed_data = re.compile(self.front_email_pattern + re.escape(email) + self.back_email_pattern).sub(
                        "<email>", mixed_data, count=1)
                except EmailNotValidError as err:
                    # 日志打印无效的电子邮件地址
                    logger.info("email is abnormal email form: %s", err)
        return mixed_data[1:-1]
