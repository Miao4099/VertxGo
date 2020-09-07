package com.runyu.user

/**
 * 此对象专门用于将数据库中的user字段对应，便于CusUser转换
 * 注意字段是否是空的
 */
class UserAdaptor{
    var user_name:String=""
    var user_id:String=""
    var user_password:String=""
    var user_role:String=""
    var user_sex:Int=1
    var user_type:Int=0
    var user_avatar:String?=""
    var user_url:String?=""
    var user_country:String?=""
    var user_province:String?=""
    var user_city:String?=""
    var created_time:String?=""
    var updated_time:String?=""
    var openid:String?=""
    var user_state:Int=0
    var wx_name:String?=""
    var memo:String?=""
}