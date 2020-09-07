package com.runyu.std

import com.runyu.std.Const.Prompt.BAD_RESPONSE
import com.runyu.std.Const.Prompt.CANT_BE_HERE
import com.runyu.std.Const.Prompt.NO_RESPONSE
import com.runyu.std.Const.Prompt.USER_BAD_PASSWROD
import com.runyu.std.Const.Prompt.USER_DISABLED
import com.runyu.std.Const.Prompt.USER_DUPLICATED
import com.runyu.std.Const.Prompt.USER_NOT_FOUND
import com.runyu.std.Const.Prompt.USER_NOT_LOGIN
import com.runyu.std.Const.Prompt.USER_NO_RIGHT
import com.runyu.std.Const.Prompt.USER_PASSWROD_NOT_SAME
import com.runyu.std.Const.Prompt.VAL_BAD_CHECK
import com.runyu.std.Const.Prompt.VAL_BAD_FILTER
import com.runyu.std.Const.Prompt.VAL_BAD_ID
import com.runyu.std.Const.Prompt.VAL_BAD_PARAMETER
import com.runyu.std.Const.Prompt.VAL_BAD_SORTER
import com.runyu.std.Const.Prompt.VAL_FIELDS_MISSED
import com.runyu.std.Const.Prompt.VAL_NULL_POINTER

/**
 * Created by sheng on 2017/5/31.
 */
object Const{

    public object MsgBody{
        public val MSG_ID="msg_id";
        public val MSG_FROM="where";
        public val MSG_TO="to";
        public val MSG_PATH="path";
        public val MSG_USER_ID="user_id";
        public val MSG_USER_NAME="user_name";
        public val MSG_USER_AVATAR="user_avatar";
        public val MSG_CONFIG="config";

        public val MSG_DB_CMD="db_sql";
        public val MSG_DB_QUERY="db_query";
        public val MSG_DB_INSERT="db_insert";

        public val MSG_RESPONSE="response";
        public val MSG_QUESTION="question";
        public val MSG_INFO="info";
        public val MSG_ANSWER="answer";
        public val MSG_QUERY_COUNT="query_count";

        public val MSG_ASSEMBLY="assembly";
        public val MSG_FORMAT="format";
        public val MSG_SRV_ID="srv_id";
        public val MSG_SRV_NAME="srv_name";
        public val MSG_SRV_URL="srv_url";
        public val MSG_SRV_INFO="srv_info";
        public val MSG_SRV_LIST="srv_list";
        public val MSG_ARRAY="array";
        public val MSG_ADDITIONAL="additional";
        public val MSG_PASS="pass";
        public val MSG_VERIFY="verify";
        public val MSG_GET_WORDS="get_words";
        public val MSG_ERR_CODE="code";
        public val MSG_GET_ALIAS="get_alias";
        public val MSG_BAD_AT="bad_at";

        public val MSG_WX_TYPE="wx_type";
        public val MSG_WX_USER="wx_user";
        public val MSG_WX_INPUT="wx_input";
        public val MSG_WX_OUTPUT="wx_output";
        public val MSG_DB_BATCH_CMD="db_batch_cmd"
        public val MSG_DB_BATCH_KEY="db_batch_key"
        /////////////////////////////////////////////////
        public val MSG_LOG_API="log_api"

    }

    public object MsgID{
        public val MSG_INIT_WORKER="MSG_INIT_WORKER";
        public val MSG_DUMMY="MSG_DUMMY";
        public val MSG_GET_INFO="MSG_GET_INFO";
        public val MSG_SELF_TEST="MSG_SELF_TEST";
        public val MSG_SHUTDOWN="MSG_SHUTDOWN";
        public val MSG_RESTART="MSG_RESTART";
        public val MSG_CONFIG="MSG_CONFIG";
        public val MSG_CONSUME="MSG_CONSUME";
        public val MSG_ASSEMBLY_REQ="MSG_ASSEMBLY_REQ";
        public val MSG_ASSEMBLY_RSP="MSG_ASSEMBLY_RSP";
        public val MSG_ASSEMBLY="MSG_ASSEMBLY";
        public val MSG_DB_QUERY ="MSG_DB_QUERY";
        public val MSG_DB_INSERT ="MSG_DB_INSERT";

        public val MSG_H5_QUERY ="MSG_H5_QUERY";
        public val MSG_WX_QUERY ="MSG_WX_QUERY";
        public val MSG_DB_BATCH_QUERY="MSG_DB_BATCH_QUERY"
        public val MSG_DB_BATCH_ACTION="MSG_DB_BATCH_ACTION"

        public val MSG_POST_JSON="MSG_POST_JSON"


        public val MSG_DB_SINGLE_QUERY ="MSG_DB_SINGLE_QUERY";
        public val MSG_DB_SINGLE_ACTION ="MSG_DB_SINGLE_ACTION";
        public val MSG_DB_TRANSACTION ="MSG_DB_TRANSACTION";
        public val MSG_DB_QUERIES ="MSG_DB_QUERIES";
    }





    public object Prompt{
        val NO_RESPONSE="后台升级中..."
        val BAD_RESPONSE="后台升级中..."
        val GOOD_RESPONSE="操作已成功完成"
        val USER_NOT_LOGIN="用戶沒有登录或用户信息不完整"
        val USER_NO_RIGHT="用戶没有权限"
        val USER_FOUND="用戶成功登录"
        val USER_DISABLED="用戶被禁止"
        val USER_PASSWROD_NOT_SAME="用戶2次密码不相同，或为空密码"
        val USER_BAD_PASSWROD="用戶密码错误"
        val USER_NOT_FOUND="找不到此用戶的信息,请检查用户名或者密码"
        val USER_DUPLICATED="存在相同的用戶"
        val CANT_BE_HERE="按理说，这里不可能执行到的！！！"


        val VAL_NULL_POINTER="空指针错误"
        val VAL_FIELDS_MISSED="请检查字段是否缺失"
        val VAL_BAD_PARAMETER="请检查字段参数类型"
        val VAL_BAD_CHECK="字段数据不存在"

        val VAL_BAD_FILTER="SQL过滤器错误"
        val VAL_BAD_SORTER="SQL排序器错误"
        val VAL_BAD_ID="SQL数据错误:id完整性"

    }

    class NoResponse:SysErr(103, NO_RESPONSE)
    class BadResponse:SysErr(104, BAD_RESPONSE)

    class ErrUserNotLogin:SysErr(105, USER_NOT_LOGIN)
    class ErrUserNoRight:SysErr(106,USER_NO_RIGHT)
    class ErrUserDisabled:SysErr(107, USER_DISABLED)
    class ErrUserNotFound:SysErr(110, USER_NOT_FOUND)
    class ErrUserDuplicated:SysErr(111, USER_DUPLICATED)
    class ErrUserPasswordNotSame:SysErr(112, USER_PASSWROD_NOT_SAME)
    class ErrUserBadPassword:SysErr(113, USER_BAD_PASSWROD)


    class ErrCantBeHere:SysErr(131,CANT_BE_HERE)


    class ErrNullPointer:SysErr(20000, VAL_NULL_POINTER)
    class ErrValFail(msg:String):SysErr(20001, msg)
    class ErrFieldMissed:SysErr(20002, VAL_FIELDS_MISSED)
    class ErrBadParameter:SysErr(20003, VAL_BAD_PARAMETER)
    class ErrBadFilter:SysErr(20004, VAL_BAD_FILTER)
    class ErrBadSorter:SysErr(20005, VAL_BAD_SORTER)
    class ErrBadCheck(key:String):SysErr(20006, key+VAL_BAD_CHECK)
    class ErrBadId:SysErr(20007, VAL_BAD_ID)
}