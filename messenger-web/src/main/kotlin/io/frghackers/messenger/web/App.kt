package io.frghackers.messenger.web

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3
import react.useState

val App = FC<Props> {
    var count by useState(0)
    h1 {
        +"Messenger"
    }
    button {
        onClick = {
            count += 1
        }
        +"Click me"
    }
    h3 {
        +count.toString()
    }
}

