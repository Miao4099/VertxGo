package com.runyu.http

import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.http.HttpServerResponse
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import java.nio.charset.Charset


//inline fun HttpServerResponse.replyText(text: String, charset: Charset = Charsets.UTF_8) {
//    write(text, charset.name())
//    endIfNotYet()
//}

inline fun HttpServerResponse.replyHtml(text:String) {
    setChunked(true)
    putHeader("content-type", "text/html");
    write(text)
    end()
}

inline fun HttpServerResponse.replyJson(json: JsonObject) {
    setChunked(true)
    putHeader("content-type", "application/json");
    write(json.toString())
    end()
}

var HttpServerResponse.contentLength: Long
    get() = headers().getAll("Content-Length").distinct().single().toLong()
    set(newValue) {
        setChunked(false)
        header("Content-Length", newValue)
    }

fun HttpServerResponse.header(headerName: CharSequence, headerValue: Number): HttpServerResponse {
    return putHeader(headerName, headerValue.toString())
}

public fun HttpServerResponse.endIfNotYet() {
    if (!ended()) {
        end()
    }
}


public inline fun Router.POST(path: String, noinline requestHandler: (r: RoutingContext) -> Unit){
    //HttpAgent初始化话时，添加了默认的bodyHandler，这样才可以收到body，但是与endHandler冲突
    this.post(path).handler { r ->
      //  r.request().setExpectMultipart(true)
      //  r.request().endHandler {
            requestHandler.invoke(r)
      //  }
    }
}

public inline fun Router.GET(path: String, noinline requestHandler: (r: RoutingContext) -> Unit){
    this.get(path).handler { r ->
     //   r.request().setExpectMultipart(true)
     //   r.request().endHandler {
            requestHandler.invoke(r)
     //   }
    }

}

