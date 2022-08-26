/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.appforge.ignite.watch

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class StackChange<T> (list : ObservableList<T>, val type : StackChangeType) : ListChangeListener.Change<T>(list) {

    private var onChange = false



    override fun wasAdded() = (type == StackChangeType.PUSH)

    override fun wasRemoved() = (type == StackChangeType.POP)

    override fun next(): Boolean {
        if(onChange)
            return false
        onChange = true
        return true
    }

    override fun reset() {
        onChange = false
    }

    override fun getFrom() : Int {
        if(!onChange) {
            throw IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
        }
        return 0;
    }

    override fun getTo(): Int {
        if(!onChange) {
            throw IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
        }
        return type.changedObject.size
    }

    override fun getPermutation(): IntArray {
        return IntArray(0)
    }

    override fun getRemoved(): MutableList<T> {
        return mutableListOf()
    }

}