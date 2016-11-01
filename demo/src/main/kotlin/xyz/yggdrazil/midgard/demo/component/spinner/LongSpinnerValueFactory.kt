package xyz.yggdrazil.midgard.demo.component.spinner

import javafx.beans.NamedArg
import javafx.beans.property.LongProperty
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.SpinnerValueFactory
import javafx.util.converter.LongStringConverter

/**
 * Created by Alexandre Mommers on 24/10/16.
 */
class LongSpinnerValueFactory(@NamedArg("min") min: Long,
                                 @NamedArg("max") max: Long,
                                 @NamedArg("initialValue") initialValue: Long,
                                 @NamedArg("amountToStepBy") amountToStepBy: Long) :
        SpinnerValueFactory<Long>() {


    constructor(@NamedArg("min") min: Long,
                @NamedArg("max") max: Long) :
    this(min, max, min)


    constructor(@NamedArg("min") min: Long,
                @NamedArg("max") max: Long,
                @NamedArg("initialValue") initialValue: Long) :
    this(min, max, initialValue, 1)


    init {
        setMin(min)
        setMax(max)
        setAmountToStepBy(amountToStepBy)
        setConverter(LongStringConverter())

        valueProperty().addListener({ o, oldValue, newValue ->
            // when the value is set, we need to react to ensure it is a
            // valid value (and if not, blow up appropriately)
            if (newValue < getMin()) {
                setValue(getMin())
            } else if (newValue > getMax()) {
                setValue(getMax())
            }
        })
        setValue(if (initialValue >= min && initialValue <= max) initialValue else min)
    }


    /***********************************************************************
     *                                                                     *
     * Properties                                                          *
     *                                                                     *
     **********************************************************************/

    // --- min
    val min = object: SimpleLongProperty(this, "min") {
        override fun invalidated() {
            val currentValue = this.getValue() ?: return

            val newMin = get ()
            if (newMin > getMax()) {
                setMin(getMax())
                return
            }

            if (currentValue < newMin) {
                setValue(newMin)
            }
        }
    }

    fun setMin(value: Long)
    {
        min.set(value)
    }
    fun getMin(): Long
    {
        return min.get()
    }

    fun  minProperty(): LongProperty
    {
        return min
    }

    val max = object: SimpleLongProperty(this, "max")
    {
        override fun invalidated() {
            val currentValue = getValue()
            if (currentValue == null) {
                return
            }

            val newMax = get ()
            if (newMax < getMin()) {
                setMax(getMin())
                return
            }

            if (currentValue > newMax) {
                setValue(newMax)
            }
        }
    }

    fun setMax(value: Long)
    {
        max.set(value)
    }
    fun getMax(): Long
    {
        return max.get()
    }

    fun maxProperty(): LongProperty
    {
        return max
    }

    val amountToStepBy = SimpleLongProperty(this, "amountToStepBy")
    fun setAmountToStepBy(value: Long)
    {
        amountToStepBy.set(value)
    }
    fun getAmountToStepBy(): Long
    {
        return amountToStepBy.get()
    }
    /**
     * Sets the amount to increment or decrement by, per step.
     */
    fun amountToStepByProperty(): LongProperty
    {
        return amountToStepBy
    }

    override fun decrement(steps: Int)
    {
        val min = getMin()
        val max = getMax()
        val newIndex = getValue() - steps * getAmountToStepBy()
        wrapValue (newIndex, min, max)
        value = if (newIndex >= min) newIndex else if (isWrapAround()) wrapValue (newIndex, min, max)+1 else min
    }

    override fun increment(steps: Int)
    {
        val min = getMin()
        val max = getMax()
        val currentValue = getValue()
        val newIndex = currentValue + steps * getAmountToStepBy()
        value = if (newIndex <= max)newIndex else if (isWrapAround())  wrapValue (newIndex, min, max)-1 else max
    }

    internal fun wrapValue(value: Long, min: Long, max: Long): Long {
        if (max == 0L) {
            throw RuntimeException()
        }

        var r = value % max
        if (r > min && max < min) {
            r = r + max - min
        } else if (r < min && max > min) {
            r = r + max - min
        }
        return r
    }

}