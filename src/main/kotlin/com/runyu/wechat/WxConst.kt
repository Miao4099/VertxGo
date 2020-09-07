package com.runyu.wechat



/**
 * Created by sheng on 2017/10/24.
 */
object WxConst {
    object FieldName {
        public val FROM_USERNAME = "FromUserName"
        public val TO_USERNAME = "ToUserName"
        public val CONTENT = "Content"
        public val CREATE_TIME = "CreateTime"
        public val MSG_TYPE = "MsgType"
        public val MSG_ID = "MsgId"
        public val MEDIA_ID = "MediaId"
        public val PIC_URL = "PicUrl"
        public val TITLE="Title"
        public val DESCRIPTION="Description"
        public val URL="Url"
        public val ARTICLE_COUNT="ArticleCount"
        public val EVENT="Event"
        public val LATITUDE="Latitude"
        public val LONGITUDE="Longitude"
        public val PRECISION="Precision"
        public val EVENT_KEY="EventKey"
        public val TICKET="Ticket"
        public val FORMAT="Format"
        public val RECOGNITION="Recognition"
    }
    object CardType {
        public val CARD_TEXT = "text"
        public val CARD_ARTICLE = "article"
        public val CARD_VOICE = "voice"
        public val CARD_IMAGE = "image"
    }
    object MsgId{
        public val MSG_CONSUME1 = "MSG_CONSUME1"
        public val MSG_CONSUME2 = "MSG_CONSUME2"
    }
}