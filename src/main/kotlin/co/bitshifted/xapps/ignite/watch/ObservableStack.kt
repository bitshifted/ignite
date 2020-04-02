/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.watch

import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import java.util.*

class ObservableStack<T> : SimpleListProperty<T>() {

    private val stack = LinkedList<T>()

    init {
        set(FXCollections.observableList(stack))
    }

   fun push(element: T) {
       stack.push(element)
       val type = StackChangeType.PUSH
       type.changedObject = listOf(element)
       fireValueChangedEvent(StackChange(get(), type))
   }

    fun pop(): T {
        val temp = stack.pop()
        val type = StackChangeType.POP
        type.changedObject = listOf(temp)
        fireValueChangedEvent(StackChange(get(), type))
        return temp
    }
}