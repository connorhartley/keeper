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
package io.github.connorhartley.guardian.sequence.action;

import io.github.connorhartley.guardian.context.Context;
import io.github.connorhartley.guardian.context.ContextProvider;
import io.github.connorhartley.guardian.context.ContextBuilder;
import io.github.connorhartley.guardian.sequence.condition.Condition;
import io.github.connorhartley.guardian.sequence.condition.ConditionResult;
import io.github.connorhartley.guardian.sequence.report.ReportType;
import io.github.connorhartley.guardian.sequence.report.SequenceReport;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Action<T extends Event> {

    private final List<Condition> conditions = new ArrayList<>();
    private final List<Condition> successfulListeners = new ArrayList<>();
    private final List<Condition> failedListeners = new ArrayList<>();

    private final ContextProvider contextProvider;

    private final Class<T> event;

    private int after;
    private int before;

    private int delay;
    private int expire;

    private List<Context> contexts;

    private SequenceReport sequenceReport;
    private ContextBuilder contextBuilder;

    Action(ContextProvider contextProvider, Class<T> event, SequenceReport sequenceReport, ContextBuilder contextBuilder, Condition... conditions) {
        this(contextProvider, event, sequenceReport, contextBuilder);
        this.conditions.addAll(Arrays.asList(conditions));
    }

    public Action(ContextProvider contextProvider, Class<T> event, SequenceReport sequenceReport, ContextBuilder contextBuilder, List<Condition> conditions) {
        this(contextProvider, event, sequenceReport, contextBuilder);
        this.conditions.addAll(conditions);
    }

    public Action(ContextProvider contextProvider, Class<T> event, SequenceReport sequenceReport, ContextBuilder contextBuilder) {
        this.contextProvider = contextProvider;
        this.event = event;
        this.sequenceReport = sequenceReport;
        this.contextBuilder = contextBuilder;

        this.contexts = new ArrayList<>();
    }

    public void addContext(Context context) {
        this.contexts.add(context);
    }

    public void testContext(User user, T event) {
        this.contextBuilder.getContexts().forEach(actionContextClass -> {
            try {
                this.contextProvider.getContextController().construct(actionContextClass, user, event).ifPresent(object -> {
                    ((Context<?>) object).asTimed().ifPresent(timed -> timed.start(user, event));
                    this.contexts.add((Context) object);
                });
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    public void suspendContext() {
        this.contexts.forEach(context -> this.contextProvider.getContextController().suspend(context));
    }

    void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    void waitAfter(int after) { this.after = after; }

    void waitBefore(int before) { this.before = before; }

    void setDelay(int delay) {
        this.delay = delay;
    }

    void setExpire(int expire) {
        this.expire = expire;
    }

    public void updateReport(SequenceReport sequenceReport) {
        this.sequenceReport = sequenceReport;
    }

    public void succeed(User user, T event) {
        this.successfulListeners.forEach(callback -> {
            ConditionResult testResult = callback.test(user, event, this.contexts, this.sequenceReport);

            this.sequenceReport =
                SequenceReport.of(testResult.getSequenceReport()).append(ReportType.TEST, testResult.hasPassed()).build();
        });
    }

    public boolean fail(User user, T event) {
        return this.failedListeners.stream()
                .anyMatch(callback -> {
                    ConditionResult testResult = callback.test(user, event, this.contexts, this.sequenceReport);

                    this.sequenceReport = SequenceReport.of(testResult.getSequenceReport()).append(ReportType.TEST,
                            testResult.hasPassed()).build();

                    return testResult.hasPassed();
                });
    }

    public boolean testConditions(User user, T event) {
        return !this.conditions.stream()
                .anyMatch(condition -> {
                    ConditionResult testResult = condition.test(user, event, this.contexts, this.sequenceReport);

                    this.sequenceReport = SequenceReport.of(testResult.getSequenceReport()).append(ReportType.TEST,
                            testResult.hasPassed()).build();

                    return !testResult.hasPassed();
                });
    }

    public int getAfter() { return this.after; }

    public int getBefore() { return this.before; }

    public int getDelay() {
        return this.delay;
    }

    public int getExpire() {
        return this.expire;
    }

    public Class<T> getEvent() {
        return this.event;
    }

    public List<Context> getContext() {
        return this.contexts;
    }

    public ContextBuilder getContextBuilder() { return this.contextBuilder; }

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public SequenceReport getSequenceReport() {
        return this.sequenceReport;
    }

    void onSuccess(Condition condition) {
        this.successfulListeners.add(condition);
    }

    void onFailure(Condition condition) {
        this.failedListeners.add(condition);
    }

}