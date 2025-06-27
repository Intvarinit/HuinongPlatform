huinong_platform: schema
    + tables
        compost_progress_log: table
            --  堆肥批次进度日志表
            + columns
                id: bigint(20) NN auto_increment = 9
                    --  主键ID
                compost_id: bigint(20) NN
                    --  堆肥批次ID
                time: datetime NN
                    --  记录时间
                temperature: decimal(5,2)
                    --  温度(℃)
                humidity: decimal(5,2)
                    --  湿度(%)
                ph_value: decimal(4,2)
                    --  pH值
                remark: varchar(500)
                    --  备注
                create_time: datetime NN default CURRENT_TIMESTAMP
                    --  创建时间
                update_time: datetime NN default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
                    --  更新时间
                deleted: tinyint(4) NN default 0
                    --  是否删除：0-未删除，1-已删除
            + indices
                idx_compost_id: index (compost_id) type btree
            + keys
                #1: PK (id) (underlying index PRIMARY)
        compost_record: table
            --  堆肥记录表
            + columns
                id: bigint(20) NN auto_increment = 4
                    --  主键ID
                recovery_id: bigint(20) NN
                    --  回收记录ID
                batch_no: varchar(50) NN
                    --  批次号
                start_time: datetime NN
                    --  开始时间
                end_time: datetime
                    --  结束时间
                status: tinyint(4) NN default 0
                    --  状态：0-进行中，1-已完成，2-异常
                temperature: decimal(5,2)
                    --  温度(℃)
                humidity: decimal(5,2)
                    --  湿度(%)
                ph_value: decimal(4,2)
                    --  pH值
                remark: varchar(500)
                    --  备注
                create_time: datetime NN default CURRENT_TIMESTAMP
                    --  创建时间
                update_time: datetime NN default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
                    --  更新时间
                deleted: tinyint(4) NN default 0
                    --  是否删除：0-未删除，1-已删除
            + indices
                idx_recovery_id: index (recovery_id) type btree
            + keys
                #1: PK (id) (underlying index PRIMARY)
                uk_batch_no: AK (batch_no)
        inspection_record: table
            --  抽检记录表
            + columns
                id: bigint(20) NN auto_increment = 7
                    --  主键ID
                compost_id: bigint(20) NN
                    --  堆肥记录ID
                inspector_id: bigint(20) NN
                    --  检查人ID
                inspection_time: datetime NN
                    --  检查时间
                result: tinyint(4) NN
                    --  结果：0-不合格，1-合格
                temperature: decimal(5,2)
                    --  温度(℃)
                humidity: decimal(5,2)
                    --  湿度(%)
                ph_value: decimal(4,2)
                    --  pH值
                remark: varchar(500)
                    --  备注
                create_time: datetime NN default CURRENT_TIMESTAMP
                    --  创建时间
                update_time: datetime NN default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
                    --  更新时间
                deleted: tinyint(4) NN default 0
                    --  是否删除：0-未删除，1-已删除
            + indices
                idx_compost_id: index (compost_id) type btree
            + keys
                #1: PK (id) (underlying index PRIMARY)
        operation_log: table
            + columns
                id: bigint(20) NN auto_increment = 1
                user_id: bigint(20)
                username: varchar(50)
                operation: varchar(100)
                method: varchar(200)
                params: text
                ip: varchar(50)
                result: varchar(20)
                error_msg: text
                create_time: datetime
            + keys
                #1: PK (id) (underlying index PRIMARY)
        recovery_record: table
            --  回收记录表
            + columns
                id: bigint(20) NN auto_increment = 2
                    --  主键ID
                user_id: bigint(20) NN
                    --  用户ID
                crop_type: varchar(50) NN
                    --  作物类型
                weight: decimal(10,2) NN
                    --  重量(kg)
                location: varchar(200) NN
                    --  回收地点
                status: tinyint(4) NN default 0
                    --  状态：0-待审核，1-已通过，2-已拒绝，3-已完成
                remark: varchar(500)
                    --  备注
                operator_id: bigint(20)
                    --  操作人ID
                appointment_time: datetime
                    --  预约时间
                create_time: datetime NN default CURRENT_TIMESTAMP
                    --  创建时间
                update_time: datetime NN default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
                    --  更新时间
                deleted: tinyint(4) NN default 0
                    --  是否删除：0-未删除，1-已删除
            + indices
                idx_user_id: index (user_id) type btree
                idx_status: index (status) type btree
            + keys
                #1: PK (id) (underlying index PRIMARY)
        sys_user: table
            --  用户表
            + columns
                id: bigint(20) NN auto_increment = 4
                    --  主键ID
                username: varchar(50) NN
                    --  用户名
                password: varchar(100) NN
                    --  密码
                phone: varchar(20) NN
                    --  手机号
                real_name: varchar(50)
                    --  真实姓名
                user_type: tinyint(4) NN default 1
                    --  用户类型：1-农户，2-管理员
                address: varchar(200)
                    --  地址
                status: tinyint(4) NN default 1
                    --  状态：0-禁用，1-启用
                create_time: datetime NN default CURRENT_TIMESTAMP
                    --  创建时间
                update_time: datetime NN default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
                    --  更新时间
                deleted: tinyint(4) NN default 0
                    --  是否删除：0-未删除，1-已删除
            + keys
                #1: PK (id) (underlying index PRIMARY)
                uk_username: AK (username)
                uk_phone: AK (phone)
        user_role: table
            + columns
                id: bigint(20) NN auto_increment = 4
                user_type: bigint(20) NN
                role_code: varchar(32) NN
                create_time: datetime default CURRENT_TIMESTAMP
            + keys
                #1: PK (id) (underlying index PRIMARY)
                user_id: AK (user_type, role_code)
