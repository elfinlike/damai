create table bonss_ventilator.device
(
    id                    bigint auto_increment comment '设备唯一ID'
        primary key,
    name                  varchar(100)      not null comment '设备名称',
    device_code           varchar(50)       not null comment '设备编码（唯一标识）',
    product_code          varchar(40)       not null comment '所属产品',
    connection_guide      longtext null comment '连接向导配置（富文本，如HTML格式）',
    device_image          varchar(255) null comment '设备图片URL',
    create_time           datetime          not null comment '记录创建时间',
    update_time           datetime(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3) comment '记录最后更新时间',
    first_activation_time datetime(3)                              null comment '设备首次激活时间',
    last_online_time      datetime(3)                              null comment '最后一次在线时间',
    last_offline_time     datetime(3)                              null comment '最后一次离线时间',
    last_run_time         datetime(3)                              null comment '最后一次运行开始时间',
    last_stop_time        datetime(3)                              null comment '最后一次停止时间',
    online_status         tinyint default 0 not null comment '在线状态：0-离线，1-在线',
    run_status            tinyint default 0 not null comment '运行状态：0-停止，1-运行，2-故障，3-待机',
    other_info_id         bigint null comment '设备其他信息ID',
    remark                varchar(500) null comment '备注信息'
) comment '设备信息表';

create table bonss_ventilator.family
(
    id          bigint auto_increment comment '家庭ID'
        primary key,
    name        varchar(100) not null comment '家庭名称',
    creator_id  bigint       not null comment '创建者用户ID（默认管理员）',
    qr_code     text null comment '家庭二维码（Base64字符串或链接）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) null comment '备注'
) comment '家庭表';

create table bonss_ventilator.family_member
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    family_id     bigint not null comment '家庭ID',
    user_id       bigint not null comment '用户ID',
    role          int null comment '成员角色（0=管理员，1=普通成员）',
    status        int null comment '加入状态（0=待审批，1=已加入，2=已拒绝）',
    request_time  datetime    default CURRENT_TIMESTAMP null comment '申请加入时间',
    approved_time datetime null comment '管理员批准加入的时间',
    create_by     varchar(64) default '' null comment '创建者',
    create_time   datetime null comment '创建时间',
    update_by     varchar(64) default '' null comment '更新者',
    update_time   datetime null comment '更新时间',
    remark        varchar(500) null comment '备注'
) comment '家庭成员表';

create table bonss_ventilator.message
(
    id               bigint auto_increment comment '消息ID'
        primary key,
    message_type     int                                   not null comment '消息主类型（100-提示消息,200-设备告警信息,300-系统与维护消息,400-家庭消息）',
    message_sub_type int                                   not null comment '消息子类型（101-呼吸暂停事件....）',
    title            varchar(128)                          not null comment '消息标题',
    content          text                                  not null comment '消息内容',
    sender_type      varchar(32) default 'SYSTEM' null comment '发送者类型（SYSTEM/USER/DEVICE）',
    receiver_id      varchar(64)                           not null comment '接收者ID',
    receiver_type    varchar(32)                           not null comment '接收者类型（USER/FAMILY/DEVICE）',
    status           tinyint(1)                            not null comment '消息状态（0-未读/1-已读）',
    push_time        datetime null comment '推送时间',
    read_time        datetime null comment '阅读时间',
    create_time      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag         char null comment '删除标志（0代表存在 1代表删除）'
) comment '消息中心表';

create table bonss_ventilator.message_type
(
    id          bigint auto_increment comment '自增主键'
        primary key,
    code        int         not null comment '消息类型编码',
    name        varchar(50) not null comment '消息类型名称',
    parent_code int null comment '父类型编码（主类型为null）',
    description varchar(255) null comment '消息类型描述',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) null comment '备注'
) comment '消息类型管理表';

create table bonss_ventilator.sys_config
(
    config_id    int auto_increment comment '参数主键'
        primary key,
    config_name  varchar(100) default '' null comment '参数名称',
    config_key   varchar(100) default '' null comment '参数键名',
    config_value varchar(500) default '' null comment '参数键值',
    config_type  char         default 'N' null comment '系统内置（Y是 N否）',
    create_by    varchar(64)  default '' null comment '创建者',
    create_time  datetime null comment '创建时间',
    update_by    varchar(64)  default '' null comment '更新者',
    update_time  datetime null comment '更新时间',
    remark       varchar(500) null comment '备注'
) comment '参数配置表';

create table bonss_ventilator.sys_dept
(
    dept_id     bigint auto_increment comment '部门id'
        primary key,
    parent_id   bigint      default 0 null comment '父部门id',
    ancestors   varchar(50) default '' null comment '祖级列表',
    dept_name   varchar(30) default '' null comment '部门名称',
    order_num   int         default 0 null comment '显示顺序',
    leader      varchar(20) null comment '负责人',
    phone       varchar(11) null comment '联系电话',
    email       varchar(50) null comment '邮箱',
    status      char        default '0' null comment '部门状态（0正常 1停用）',
    del_flag    char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime null comment '更新时间'
) comment '部门表';

create table bonss_ventilator.sys_dict_data
(
    dict_code   bigint auto_increment comment '字典编码'
        primary key,
    dict_sort   int          default 0 null comment '字典排序',
    dict_label  varchar(100) default '' null comment '字典标签',
    dict_value  varchar(100) default '' null comment '字典键值',
    dict_type   varchar(100) default '' null comment '字典类型',
    css_class   varchar(100) null comment '样式属性（其他样式扩展）',
    list_class  varchar(100) null comment '表格回显样式',
    is_default  char         default 'N' null comment '是否默认（Y是 N否）',
    status      char         default '0' null comment '状态（0正常 1停用）',
    create_by   varchar(64)  default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64)  default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) null comment '备注'
) comment '字典数据表';

create table bonss_ventilator.sys_dict_type
(
    dict_id     bigint auto_increment comment '字典主键'
        primary key,
    dict_name   varchar(100) default '' null comment '字典名称',
    dict_type   varchar(100) default '' null comment '字典类型',
    status      char         default '0' null comment '状态（0正常 1停用）',
    create_by   varchar(64)  default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64)  default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) null comment '备注',
    constraint dict_type
        unique (dict_type)
) comment '字典类型表';

create table bonss_ventilator.sys_logininfor
(
    info_id        bigint auto_increment comment '访问ID'
        primary key,
    user_name      varchar(50)  default '' null comment '用户账号',
    ipaddr         varchar(128) default '' null comment '登录IP地址',
    login_location varchar(255) default '' null comment '登录地点',
    browser        varchar(50)  default '' null comment '浏览器类型',
    os             varchar(50)  default '' null comment '操作系统',
    status         char         default '0' null comment '登录状态（0成功 1失败）',
    msg            varchar(255) default '' null comment '提示消息',
    login_time     datetime null comment '访问时间'
) comment '系统访问记录';

create index idx_sys_logininfor_lt
    on bonss_ventilator.sys_logininfor (login_time);

create index idx_sys_logininfor_s
    on bonss_ventilator.sys_logininfor (status);

create table bonss_ventilator.sys_menu
(
    menu_id     bigint auto_increment comment '菜单ID'
        primary key,
    menu_name   varchar(50) not null comment '菜单名称',
    parent_id   bigint       default 0 null comment '父菜单ID',
    order_num   int          default 0 null comment '显示顺序',
    path        varchar(200) default '' null comment '路由地址',
    component   varchar(255) null comment '组件路径',
    query       varchar(255) null comment '路由参数',
    route_name  varchar(50)  default '' null comment '路由名称',
    is_frame    int          default 1 null comment '是否为外链（0是 1否）',
    is_cache    int          default 0 null comment '是否缓存（0缓存 1不缓存）',
    menu_type   char         default '' null comment '菜单类型（M目录 C菜单 F按钮）',
    visible     char         default '0' null comment '菜单状态（0显示 1隐藏）',
    status      char         default '0' null comment '菜单状态（0正常 1停用）',
    perms       varchar(100) null comment '权限标识',
    icon        varchar(100) default '#' null comment '菜单图标',
    create_by   varchar(64)  default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64)  default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) default '' null comment '备注'
) comment '菜单权限表';

create table bonss_ventilator.sys_notice
(
    notice_id      int auto_increment comment '公告ID'
        primary key,
    notice_title   varchar(50) not null comment '公告标题',
    notice_type    char        not null comment '公告类型（1通知 2公告）',
    notice_content longblob null comment '公告内容',
    status         char        default '0' null comment '公告状态（0正常 1关闭）',
    create_by      varchar(64) default '' null comment '创建者',
    create_time    datetime null comment '创建时间',
    update_by      varchar(64) default '' null comment '更新者',
    update_time    datetime null comment '更新时间',
    remark         varchar(255) null comment '备注'
) comment '通知公告表';

create table bonss_ventilator.sys_oper_log
(
    oper_id        bigint auto_increment comment '日志主键'
        primary key,
    title          varchar(50)   default '' null comment '模块标题',
    business_type  int           default 0 null comment '业务类型（0其它 1新增 2修改 3删除）',
    method         varchar(200)  default '' null comment '方法名称',
    request_method varchar(10)   default '' null comment '请求方式',
    operator_type  int           default 0 null comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name      varchar(50)   default '' null comment '操作人员',
    dept_name      varchar(50)   default '' null comment '部门名称',
    oper_url       varchar(255)  default '' null comment '请求URL',
    oper_ip        varchar(128)  default '' null comment '主机地址',
    oper_location  varchar(255)  default '' null comment '操作地点',
    oper_param     varchar(2000) default '' null comment '请求参数',
    json_result    varchar(2000) default '' null comment '返回参数',
    status         int           default 0 null comment '操作状态（0正常 1异常）',
    error_msg      varchar(2000) default '' null comment '错误消息',
    oper_time      datetime null comment '操作时间',
    cost_time      bigint        default 0 null comment '消耗时间'
) comment '操作日志记录';

create index idx_sys_oper_log_bt
    on bonss_ventilator.sys_oper_log (business_type);

create index idx_sys_oper_log_ot
    on bonss_ventilator.sys_oper_log (oper_time);

create index idx_sys_oper_log_s
    on bonss_ventilator.sys_oper_log (status);

create table bonss_ventilator.sys_post
(
    post_id     bigint auto_increment comment '岗位ID'
        primary key,
    post_code   varchar(64) not null comment '岗位编码',
    post_name   varchar(50) not null comment '岗位名称',
    post_sort   int         not null comment '显示顺序',
    status      char        not null comment '状态（0正常 1停用）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime null comment '更新时间',
    remark      varchar(500) null comment '备注'
) comment '岗位信息表';

create table bonss_ventilator.sys_role
(
    role_id             bigint auto_increment comment '角色ID'
        primary key,
    role_name           varchar(30)  not null comment '角色名称',
    role_key            varchar(100) not null comment '角色权限字符串',
    role_sort           int          not null comment '显示顺序',
    data_scope          char        default '1' null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly tinyint(1)  default 1   null comment '菜单树选择项是否关联显示',
    dept_check_strictly tinyint(1)  default 1   null comment '部门树选择项是否关联显示',
    status              char         not null comment '角色状态（0正常 1停用）',
    del_flag            char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by           varchar(64) default '' null comment '创建者',
    create_time         datetime null comment '创建时间',
    update_by           varchar(64) default '' null comment '更新者',
    update_time         datetime null comment '更新时间',
    remark              varchar(500) null comment '备注'
) comment '角色信息表';

create table bonss_ventilator.sys_role_dept
(
    role_id bigint not null comment '角色ID',
    dept_id bigint not null comment '部门ID',
    primary key (role_id, dept_id)
) comment '角色和部门关联表';

create table bonss_ventilator.sys_role_menu
(
    role_id bigint not null comment '角色ID',
    menu_id bigint not null comment '菜单ID',
    primary key (role_id, menu_id)
) comment '角色和菜单关联表';

create table bonss_ventilator.sys_user
(
    user_id         bigint auto_increment comment '用户ID'
        primary key,
    dept_id         bigint null comment '部门ID',
    user_name       varchar(30) not null comment '用户账号',
    nick_name       varchar(30) not null comment '用户昵称',
    user_type       varchar(2)   default '00' null comment '用户类型（00系统用户）',
    email           varchar(50)  default '' null comment '用户邮箱',
    phonenumber     varchar(11)  default '' null comment '手机号码',
    sex             char         default '0' null comment '用户性别（0男 1女 2未知）',
    avatar          varchar(100) default '' null comment '头像地址',
    password        varchar(100) default '' null comment '密码',
    status          char         default '0' null comment '账号状态（0正常 1停用）',
    del_flag        char         default '0' null comment '删除标志（0代表存在 2代表删除）',
    login_ip        varchar(128) default '' null comment '最后登录IP',
    login_date      datetime null comment '最后登录时间',
    pwd_update_date datetime null comment '密码最后更新时间',
    create_by       varchar(64)  default '' null comment '创建者',
    create_time     datetime null comment '创建时间',
    update_by       varchar(64)  default '' null comment '更新者',
    update_time     datetime null comment '更新时间',
    remark          varchar(500) null comment '备注'
) comment '用户信息表';

create table bonss_ventilator.sys_admin
(
    admin_id        bigint auto_increment comment '用户ID'
        primary key,
    user_name       varchar(30) not null comment '用户账号',
    nick_name       varchar(30) not null comment '用户昵称',
    user_type       varchar(2)   default '00' null comment '用户类型（00系统用户）',
    phonenumber     varchar(11)  default '' null comment '手机号码',
    sex             char         default '0' null comment '用户性别（0男 1女 2未知）',
    avatar          varchar(100) default '' null comment '头像地址',
    password        varchar(100) default '' null comment '密码',
    status          char         default '0' null comment '账号状态（0正常 1停用）',
    del_flag        char         default '0' null comment '删除标志（0代表存在 2代表删除）',
    login_ip        varchar(128) default '' null comment '最后登录IP',
    login_date      datetime null comment '最后登录时间',
    pwd_update_date datetime null comment '密码最后更新时间',
    create_by       varchar(64)  default '' null comment '创建者',
    create_time     datetime null comment '创建时间',
    update_by       varchar(64)  default '' null comment '更新者',
    update_time     datetime null comment '更新时间',
    remark          varchar(500) null comment '备注'
) comment '管理员信息表';

create table bonss_ventilator.sys_admin_role
(
    admin_id bigint not null comment '用户ID',
    role_id  bigint not null comment '角色ID',
    primary key (admin_id, role_id)
) comment '用户和角色关联表';

create table bonss_ventilator.sys_user_post
(
    user_id bigint not null comment '用户ID',
    post_id bigint not null comment '岗位ID',
    primary key (user_id, post_id)
) comment '用户与岗位关联表';

create table bonss_ventilator.sys_user_role
(
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (user_id, role_id)
) comment '用户和角色关联表';

