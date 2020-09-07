package com.runyu.com.runyu.app.`val`

import com.runyu.common.IdCard
import com.runyu.sql.ValException
import com.runyu.sql.Validator
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.regex.Pattern


class ValBase64(var name:String="数据"): Validator(){

    override fun validate(v: Any?):Any?{
        var value = v as String?

        if(null==value)return value

        if (value!!.contains(" "))
            throw ValException("${name}不能为空格")
        if (!isBase64(value))
            throw ValException("${name}含有非法字符,不是Base64编码")
        return v
    }

    private fun isBase64(str: String): Boolean {
        val base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"
        return Pattern.matches(base64Pattern, str)
    }
}


open class ValHyperKind() : Validator() {
    override fun validate(value: Any?):Any? {
        if (null == value)
            throw ValException("组件名称不能为空")

        if (!setOf("home","contact","brand","shop","milestone","teahill","vip_benefits","vip_interact").contains(value.toString().toLowerCase()))
            throw ValException("组件名称不合法")
        return value
    }
}

open class ValBool(var name: String) : Validator() {
    override fun validate(value: Any?):Any? {
        if (null == value)
            throw ValException("${name}不能为空")

        if (!setOf("TRUE", "FALSE").contains(value.toString().toUpperCase()))
            throw ValException("${name}不合法")
        return value
    }
}

open class ValPrice(var name: String, var min: Double =0.0, var max: Double = 0.0) : Validator() {
    override fun validate(v: Any?):Any?{
        if (null == v)
            throw ValException("${name}不能为空")

        var value = v.toString()
        var vv =0.0
        try {
            vv = value.toDouble()
        } catch (e: NumberFormatException){
            throw ValException("${name}不合法")
        }
        if (vv < min || vv > max)
            throw ValException("${name}超出范围")

        return v
    }
}

class ValName : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("名字不能为空")
        if (value.contains(" "))
            throw ValException("名字不能包括空格")

        if (match(value, "[\\pP\\p{Punct}]"))
            throw ValException("名字中含有非法字符")

        return value
    }
}


class ValMaterialKind : Validator() {
    val roles = setOf<Int>(1,2)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("素材类型不能为空")

        if(!roles.contains(value))
            throw ValException("素材类型设置${value}超出范围")

        return value
    }
}

class ValCommentKind : Validator() {
    val roles = setOf<Int>(1,2)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("评论类型不能为空")

        if(!roles.contains(value))
            throw ValException("评论类型${value}超出范围")

        return value
    }
}

class ValCommentState : Validator() {
    val roles = setOf<Int>(1,2,3,4)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("评论状态不能为空")

        if(!roles.contains(value))
            throw ValException("评论状态${value}超出范围")

        return value
    }
}


class ValUserState : Validator() {
    val roles = setOf<Int>(0,1,2,3) //0默认 1，2未用 3.禁止
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("用户状态不能为空")

        if(!roles.contains(value))
            throw ValException("用户状态${value}超出范围")

        return value
    }
}


class ValParentName : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value!!.contains(" "))
            throw ValException("名字不能包括空格")
        if (match(value, "[\\pP\\p{Punct}]"))
            throw ValException("名字中含有非法字符")

        return value
    }
}


class ValClassName : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("班级名字不能为空")
        if (value.contains(" "))
            throw ValException("班级名字不能包括空格")
        if (match(value, "[\\pP\\p{Punct}]"))
            throw ValException("名字中含有非法字符")

        return value
    }
}


class ValDistrictsName : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("所属区名字不能为空")
        if (value.contains(" "))
            throw ValException("所属区名字不能包括空格")
        if (match(value, "[\\pP\\p{Punct}]"))
            throw ValException("名字中含有非法字符")
        return value
    }
}


class ValNationName : Validator() {
    val nations = arrayOf("汉族", "壮族", "满族", "回族", "苗族", "维吾尔族",
            "土家族", "彝族", "蒙古族", "藏族", "布依族", "侗族",
            "瑶族", "朝鲜族", "白族", "哈尼族", "哈萨克族", "黎族",
            "傣族", "畲族", "傈僳族", "仡佬族", "东乡族", "高山族",
            "拉祜族", "水族", "佤族", "纳西族", "羌族", "土族",
            "仫佬族", "锡伯族", "柯尔克孜族", "达斡尔族", "景颇族", "毛南族",
            "撒拉族", "塔吉克族", "阿昌族", "普米族", "鄂温克族", "怒族",
            "京族", "基诺族", "德昂族", "保安族", "俄罗斯族", "裕固族",
            "乌兹别克族", "门巴族", "鄂伦春族", "独龙族", "塔塔尔族", "赫哲族",
            "珞巴族", "布朗族")

    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("民族名字不能为空")
        if (value.contains(" "))
            throw ValException("民族名字不能包括空格")
        if (-1 == nations.indexOf(value) && -1 == nations.indexOf(value + "族"))
            throw ValException("名族名称不合法")

        return value
    }
}

class ValClassGrade : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null == value)
            throw ValException("年级不能为空")
        if (!(value >= 0 && value <= 5))
            throw ValException("年级设置超出范围")

        return value
    }
}

class ValAddress : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("地址不能为空")
        if (value.contains(" "))
            throw ValException("地址不能包括空格")
        if (match(value, "[\\pP\\p{Punct}]"))
            throw ValException("地址中含有非法字符")

        return value
    }
}

class ValLongitude : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("经度值不能为空")

        var reglo = "((?:[0-9]|[1-9][0-9]|1[0-7][0-9])\\.([0-9]{0,6}))|((?:180)\\.([0]{0,6}))".toRegex()
        if (!value.trim().substring(0, 10).matches(reglo))
            throw ValException("经度值不合规则")

        return value
    }
}

class ValLatitude : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("维度值不能为空")

        var reglo = "((?:[0-9]|[1-8][0-9])\\.([0-9]{0,6}))|((?:90)\\.([0]{0,6}))".toRegex()
        if (!value.trim().substring(0, 9).matches(reglo))
            throw ValException("维度值不合规则")

        return value
    }
}


class ValTel : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("电话号码不能为空")

        var reglo = "(^1\\d{10}\$)|(^\\d{3,4}-\\d{7,8}\$)".toRegex()
        if (!value.trim().matches(reglo))
            throw ValException("电话号码不合规则")

        return value
    }
}

class ValDate : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("日期不能为空")
        try {
            LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            throw ValException("日期不合法")
        }
        return value
    }
}

class ValBirthday : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("生日为空或身份证号不合法")
        if (null == LocalDate.parse(value))
            throw ValException("生日不合法或身份证号不合法")

        return value
    }
}

open class ValId(var name: String = "",var nullable:Boolean=false) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value&&!nullable)
            throw ValException("${name}id不能为空")

        //允许空值
        if(null==value)
            return value

        if (32 != value!!.length)
            throw ValException("${name}id长度不对")
        return value
    }
}

open class ValTmallLink(var nullable:Boolean=true) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value&&!nullable)
            throw ValException("天猫口令不能为空")

        //允许空值
        if(null==value)
            return value

        return value
    }
}


class ValSex(var nullable:Boolean=false) : Validator() {
    override fun validate(value: Any?):Any? {
        if (null == value&&!nullable)
            throw ValException("性别不能为空")

        //允许空值
        if(null==value)
            return value

        var ret=0
        if(value is Integer) {
            var v = value as Int?
            var valid=setOf(1,0)
            if(!valid.contains(value))
                throw ValException("性别设置超出范围")
        }else if(value is String){
            var valid= mapOf(Pair("1",1),Pair("0",0))
            ret=valid.get(value)?:throw ValException("性别设置超出范围")

        }else
            throw ValException("性别设置超出范围")

        return ret
    }
}

class ValBeltNo : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("臂带号不能为空")
        if (value.length > 9 || value.length < 3)
            throw ValException("臂带号长度在4-7位之间")
        if (!value.matches("^[0-9]*\$".toRegex()))
            throw ValException("臂带号只能是数字")

        return value
    }
}


class ValStudentNo : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("序号不能为空")
        if (!value.matches("^[0-9]*\$".toRegex()))
            throw ValException("序号只能是数字")

        return value
    }
}


class ValIdCard : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("身份证不能为空")
        if (!IdCard.isLegal(value))
            throw ValException("身份证不合法")

        return value
    }
}

class ValPassword : Validator() {
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (null == value)
            throw ValException("密码不能为空")
        if (value.length < 6)
            throw ValException("密码长度至少6位")
        if (value.length > 18)
            throw ValException("密码长度最长18位")
        if (!match(value, "[\\da-zA-Z]{6,18}"))
            throw ValException("密码只能是小写字母、大写字母和数字")

        return value
    }
}


class ValRole : Validator() {
    val roles = setOf("admin", "user", "editor","vip")
    override fun validate(name: Any?):Any? {
        var value = name as String?
        if (value.isNullOrEmpty())
            throw ValException("用户权限不能为空")
        if (value.contains(" "))
            throw ValException("用户权限不能包括空格")

        if(!roles.contains(name))
            throw ValException("权限设置${value}超出范围")

        return value
    }
}

class ValProductState : Validator() {
    val roles = setOf<Int>(0, 1, 2,3)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("产品状态不能为空")

        if(!roles.contains(value))
            throw ValException("产品状态设置${value}超出范围")

        return value
    }
}

class ValNewsState : Validator() {
    val roles = setOf<Int>(1, 2)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("新闻状态不能为空")

        if(!roles.contains(value))
            throw ValException("新闻状态设置${value}超出范围")

        return value
    }
}

class ValHyperState : Validator() {
    val roles = setOf<Int>(1, 2)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("素材状态不能为空")

        if(!roles.contains(value))
            throw ValException("素材状态设置${value}超出范围")

        return value
    }
}

class ValCommentLevel : Validator() {
    val roles = setOf<Int>(1,2,3,4,5)
    override fun validate(name: Any?):Any? {
        var value = name as Int?
        if (null==value)
            throw ValException("评价等级不能为空")

        if(!roles.contains(value))
            throw ValException("评价等级设置${value}超出范围")

        return value
    }
}

open class ValUrl(var name: String,var nullable:Boolean) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (!nullable){
            if(null == value)  throw ValException("${name}URL不能为空")
        }else{
            if(value.isNullOrEmpty())  return value
        }

        var reg = "(?:(https?|ftp|file):)?//[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]".toRegex()
        if (!value!!.trim().matches(reg))
            throw ValException("${name}URL不合规则")

        return value
    }
}



class ValBlogStatus() : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as Int?
        if (null == value)
            throw ValException("文章状态不能为空")
        if (!arrayOf(0, 1, 2).contains(value))
            throw ValException("文章状态不合规则")

        return value
    }
}


class ValPointKind() : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value)
            throw ValException("观点类型不能为空")

        if (!setOf("image", "text").contains(value))
            throw ValException("观点类型不符合要求")

        return value
    }
}

class ValPointData(var pointKind: String) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value)
            throw ValException("观点不能为空")

        when (pointKind) {
            "image" -> ValUrl("观点", false).validate(value)
            "text" -> ValText("观点", 1, 200).validate(value)
        }

        return value
    }
}


open class ValBadCheck(var name: String) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value)
            throw ValException("${name}不能为空")

        return value
    }
}


class ValMilestoneKind() : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value)
            throw ValException("阶段类型不能为空")

        if (!setOf("trace", "store").contains(value))
            throw ValException("阶段类型不符合要求")

        return value
    }
}

class ValPath() : Validator() {
    override fun validate(v: Any?):Any?{
        return v
    }
}

open class ValText(var name: String, var min: Long, var max: Long) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value)
            throw ValException("${name}不能为空")
        if (value.length.toLong() > max)
            throw ValException("${name}长度超长")
        if (value.length < min)
            throw ValException("${name}低于最短长度")

        return value
    }
}




class ValFileName(var nullable:Boolean= true) : Validator() {
    override fun validate(v: Any?):Any?{
        var value = v as String?
        if (null == value&&!nullable)
            throw ValException("文件名不能为空")

        if(value.isNullOrEmpty())return value

        var reg = "[\\s\\\\/:\\*\\?\\\"<>\\|]".toRegex()
        if (value!!.matches(reg))
            throw ValException("文件名不合规则")

        return value
    }
}





class ValBlogTitle : ValText("标题", 1, 255)

class ValBlogContent : ValText("内容", 1, 16777215)

class ValStepName : ValText("内容", 1, 128)

class ValBrief : ValText("简述", 1, 128)

class ValNote : ValText("金句", 1, 32)

class ValProductBrief : ValText("简述", 1, 128)

class ValComment : ValText("评论", 1, 512)
class ValSummary : ValText("总体评价", 1, 512)



class ValImage(name:String="图片",nullable:Boolean=false) : ValUrl(name,nullable)
class ValVideo(name:String="视频",nullable:Boolean=true) : ValUrl(name,nullable)
class ValNews(name:String="新闻",nullable:Boolean=false) : ValUrl(name,nullable)
class ValCriticUrl(name:String="评论人",nullable:Boolean=false) : ValUrl(name,nullable)
class ValAvatar(name:String="头像",nullable:Boolean=false) : ValUrl(name,nullable)
class ValProductImage(name:String="图片",nullable:Boolean=true) : ValUrl(name,nullable)

class ValIsPreSale() : ValBool("预售")
class ValIsPublish() : ValBool("发布状态")

class ValTeaPrice: ValPrice("茶叶价格",1.0,10000000.0)


class ValProductId : ValId("产品",false)
class ValUserId : ValId("用户",false)
class ValBlogId : ValId("文章",false)
class ValForId : ValId("依赖",true)
class ValHyperId : ValId("素材",true)
class ValMilestoneId : ValId("里程碑",true)


