/*
 * MIT License
 *
 * Copyright (c) 2017 Connor Hartley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ichorpowered.guardian.sponge.sequence.action.schedule;

import com.ichorpowered.guardian.api.sequence.SequenceBlueprint;
import com.ichorpowered.guardian.api.sequence.SequenceContext;
import com.ichorpowered.guardian.api.sequence.action.ActionBuilder;
import com.ichorpowered.guardian.api.sequence.action.after.AfterAction;
import com.ichorpowered.guardian.api.sequence.action.after.AfterActionBuilder;
import com.ichorpowered.guardian.api.sequence.action.observe.ObserverAction;
import com.ichorpowered.guardian.api.sequence.action.observe.ObserverActionBlueprint;
import com.ichorpowered.guardian.api.sequence.action.observe.ObserverActionBuilder;
import com.ichorpowered.guardian.api.sequence.action.schedule.ScheduleAction;
import com.ichorpowered.guardian.api.sequence.action.schedule.ScheduleActionBuilder;
import com.ichorpowered.guardian.api.sequence.process.Process;
import com.ichorpowered.guardian.api.sequence.process.ProcessResult;
import com.ichorpowered.guardian.sponge.sequence.action.after.AfterActionImpl;
import com.ichorpowered.guardian.sponge.sequence.action.observe.ObserverActionImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.event.Event;

import java.util.function.Function;

public class ScheduleActionBuilderImpl<T extends Event> implements ScheduleActionBuilder<T> {

    private final ActionBuilder<T> actionBuilder;
    private final ScheduleAction scheduleAction;

    public ScheduleActionBuilderImpl(final ActionBuilder<T> actionBuilder, final ScheduleAction scheduleAction) {
        this.actionBuilder = actionBuilder;
        this.scheduleAction = scheduleAction;
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> condition(final @NonNull Function<Process, ProcessResult> function) {
        this.scheduleAction.addCondition(function);
        return this;
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> delay(final int value) {
        this.scheduleAction.setDelay(value);
        return this;
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> expire(final int expire) {
        this.scheduleAction.setExpire(expire);
        return this;
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> period(final int period) {
        this.scheduleAction.setPeriod(period);
        return this;
    }

    @Override
    public @NonNull ObserverActionBuilder<T> observe(final @NonNull Class<T> event) {
        return this.observe(new ObserverActionImpl<>(event));
    }

    @Override
    public @NonNull ObserverActionBuilder<T> observe(final @NonNull ObserverActionBlueprint<T> actionBlueprint) {
        return this.observe(actionBlueprint.create());
    }

    @Override
    public @NonNull ObserverActionBuilder<T> observe(final @NonNull ObserverAction<T> action) {
        return this.actionBuilder.observe(action);
    }

    @Override
    public @NonNull AfterActionBuilder<T> after() {
        return this.after(new AfterActionImpl());
    }

    @Override
    public @NonNull AfterActionBuilder<T> after(final @NonNull AfterAction afterAction) {
        return this.actionBuilder.after(afterAction);
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> schedule() {
        return this.schedule(new ScheduleActionImpl());
    }

    @Override
    public @NonNull ScheduleActionBuilder<T> schedule(final @NonNull ScheduleAction scheduleAction) {
        return this.actionBuilder.schedule(scheduleAction);
    }

    @Override
    public @NonNull SequenceBlueprint<T> build(final @NonNull SequenceContext sequenceContext) {
        return this.actionBuilder.build(sequenceContext);
    }

}
