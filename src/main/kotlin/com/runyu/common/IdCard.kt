package com.runyu.common


import java.util.Calendar
import java.util.Date

object IdCard {

    fun getBirthday(str: String):String?{
        var strID=str//.replace("[\\pP\\p{Punct}]".toRegex(),"")
        if(isLegal(strID)) {
            //验证生日是否正确
            var strBirthDay = ""
            if (strID.length == 15) {
                strBirthDay = "19" + strID.substring(6, 12)
            } else {
                strBirthDay = strID.substring(6, 14)
            }

            strBirthDay=strBirthDay.substring(0,4)+"-"+strBirthDay.substring(4,6)+"-"+strBirthDay.substring(6,8)
            return strBirthDay
        }
        return null
    }

    fun isLegal(strID: String?): Boolean {
        var flag = true
        //判断输入的字符串是否为空
        if (strID == null || strID.length <= 0 || strID == "") {
            flag = false
        }
        //判断输入的字符串长度是否为15或者18
        if (strID!!.length != 15 && strID.length != 18) {
            flag = false
        }
        //判断输入的字符串是否都为数字
        if (!isDigit(strID)) {
            flag = false
        }
        //判断身份证号的前两位是否正确
        if (!isCorrectFirstTwo(strID.substring(0, 2))) {
            flag = false
        }
        //验证生日是否正确
        var strBirthDay = ""
        if (strID.length == 15) {
            strBirthDay = "19" + strID.substring(6, 12)
        } else {
            strBirthDay = strID.substring(6, 14)
        }
        if (!isCorrectBirthDay(strBirthDay)) {
            flag = false
        }
        //验证18位身份证号的校验码是否正确
        if (strID.length == 18) {
            if (!isCheckCode(strID)) {
               // flag = false 临时关闭，miao
            }
        }
        return flag
    }

    fun displayBirthDate(strID: String) {
        var strYear = ""
        var strMonth = ""
        var strDay = ""

        if (strID.length == 15) {
            strYear = "19" + strID.substring(6, 8)
            strMonth = strID.substring(8, 10)
            strDay = strID.substring(10, 12)
        }

        if (strID.length == 18) {
            strYear = strID.substring(6, 10)
            strMonth = strID.substring(10, 12)
            strDay = strID.substring(12, 14)
        }

        println("生日日期为：" + strYear + "年" + strMonth + "月" + strDay + "日")
    }

    fun isCheckCode(strID: String): Boolean {
        // 每位加权因子
        val arrWeight = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
        // 第18位校检码
        val strArrCheckCode = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
        //将身份证号前17位存入数组，进行下一步计算
        val arrID = IntArray(17)
        for (i in 0..16) {
            arrID[i] = Integer.parseInt(strID.substring(i, i + 1))
        }
        var nSum = 0
        for (i in 0..16) {
            nSum += arrID[i] * arrWeight[i]
        }
        val nIdx = nSum % 11
        val strLast = strID.substring(17)
        return if (strArrCheckCode[nIdx] == strLast) {
            true
        } else {
            false
        }
    }

    fun isCorrectBirthDay(strDate: String): Boolean {
        val nYear = Integer.parseInt(strDate.substring(0, 4))
        val nMonth = Integer.parseInt(strDate.substring(4, 6))
        val nDay = Integer.parseInt(strDate.substring(6, 8))
        //获取系统当前的日期，判断是否在当前日期之前
        val curDate = Date()
        val cal = Calendar.getInstance()
        cal.time = curDate
        if (nYear > cal.get(Calendar.YEAR)) {
            return false
        }
        //判断是否为合法月份
        if (nMonth < 1 || nMonth > 12) {
            return false
        }
        //判断是否为合法日期
        var bFlag = false
        when (nMonth) {
            1, 3, 5, 7, 8, 10, 12 -> if (nDay >= 1 && nDay <= 31) {
                bFlag = true
            }
            2 -> if (nYear % 4 == 0 && nYear % 100 != 0 || nYear % 400 == 0) {
                if (nDay >= 1 && nDay <= 29) {
                    bFlag = true
                }
            } else {
                if (nDay >= 1 && nDay <= 28) {
                    bFlag = true
                }
            }
            4, 6, 9, 11 -> if (nDay >= 1 && nDay <= 30) {
                bFlag = true
            }
        }
        return if (!bFlag) {
            false
        } else true
    }

    fun isCorrectFirstTwo(strID12: String): Boolean {
        val strArrCityCode = arrayOf(
            "11",
            "12",
            "13",
            "14",
            "15",
            "21",
            "22",
            "23",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "50",
            "51",
            "52",
            "53",
            "54",
            "61",
            "62",
            "63",
            "64",
            "65",
            "71",
            "81",
            "82",
            "91"
        )
        var bFlag = false
        for (strIdx in strArrCityCode) {
            if (strIdx.equals(strID12, ignoreCase = true)) {
                bFlag = true
                break
            }
        }
        return if (bFlag) {
            true
        } else false
    }

    fun isDigit(strID: String): Boolean {
        var nDigitCnt = 0
        val nLength = if (strID.length == 15) strID.length else strID.length - 1
        for (i in 0 until nLength) {
            val cTmp = strID[i]
            if (cTmp >= '0' && cTmp <= '9') {
                ++nDigitCnt
            }
        }
        return if (nDigitCnt != nLength) {
            false
        } else {
            true
        }
    }


}