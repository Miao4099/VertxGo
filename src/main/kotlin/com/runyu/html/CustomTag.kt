package com.runyu.html

import kotlinx.html.*

/**
 * Created by sheng on 2017/7/15.
 */

public class CUSTOM(consumer: TagConsumer<*>) :
        HTMLTag("s", consumer, emptyMap(),
                inlineTag = true,
                emptyTag = false), HtmlInlineTag {
}

public fun <T> TagConsumer<T>.custom(block: CUSTOM.() -> Unit = {}): T {
    return CUSTOM(this).visitAndFinalize(this, block)
}

public fun LI.s(block: CUSTOM.() -> Unit = {}) {
    CUSTOM(consumer).visit(block)
}
public fun LI.i(block: CUSTOM.() -> Unit = {}) {
    CUSTOM(consumer).visit(block)
}


