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
package io.github.connorhartley.guardian.sequence.condition;

import io.github.connorhartley.guardian.sequence.SequenceResult;
import io.github.connorhartley.guardian.sequence.capture.CaptureContainer;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;

/**
 * Condition
 *
 * Represents a condition that executes logic and
 * gets a {@link ConditionResult}.
 */
public interface Condition {

    /**
     * Test
     *
     * <p>A condition to check for.</p>
     *
     * @param user The user to check
     * @param event The event that caused the check
     * @param captureContainer The capture container with capture data
     * @param sequenceResult The sequenced report chained down
     * @param lastAction The time since the last action
     * @return The result of the condition
     */
    ConditionResult test(User user, Event event, CaptureContainer captureContainer, SequenceResult sequenceResult, long lastAction);

}
