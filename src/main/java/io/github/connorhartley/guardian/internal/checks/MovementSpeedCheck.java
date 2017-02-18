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
package io.github.connorhartley.guardian.internal.checks;

import io.github.connorhartley.guardian.context.ContextBuilder;
import io.github.connorhartley.guardian.detection.Detection;
import io.github.connorhartley.guardian.detection.check.Check;
import io.github.connorhartley.guardian.detection.check.CheckController;
import io.github.connorhartley.guardian.detection.check.CheckProvider;
import io.github.connorhartley.guardian.sequence.Sequence;
import io.github.connorhartley.guardian.sequence.SequenceBlueprint;
import io.github.connorhartley.guardian.sequence.SequenceBuilder;
import io.github.connorhartley.guardian.sequence.condition.ConditionResult;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.entity.MoveEntityEvent;

public class MovementSpeedCheck extends Check {

    public MovementSpeedCheck(CheckProvider checkProvider, User user) {
        super(checkProvider, user);
    }

    @Override
    public void update() {}

    @Override
    public void finish() {}

    public static class Provider implements CheckProvider {

        private final Detection detection;

        public Provider(Detection detection) {
            this.detection = detection;
        }

        @Override
        public Detection getDetection() {
            return this.detection;
        }

        @Override
        public ContextBuilder getContextTracker() {
            return null;
        }

        @Override
        public SequenceBlueprint getSequence() {
            return new SequenceBuilder(this.getDetection().getContextProvider(), this.getContextTracker())

                    // Trigger : Move Entity Event

                    .action(MoveEntityEvent.class, null)
                    .condition((user, event, contexts, sequenceResult) -> {
                        if (!user.hasPermission("guardian.detection.movementspeed.exempt")) {
                            return new ConditionResult(true, sequenceResult);
                        }
                        return new ConditionResult(false, sequenceResult);
                    })
                    .success((user, event, contexts, sequenceResult) -> {
                        // Net timing starts here.

                        return new ConditionResult(false, sequenceResult);
                    })
                    .after(20 * 2)
                    .expire(20 * 3)

                    .action(MoveEntityEvent.class)
                    .condition((user, event, contexts, sequenceResult) -> {

                        // TODO: Juice'y movement speed check here.

                        return new ConditionResult(false, sequenceResult);
                    })

                    .build(this);
        }

        @Override
        public Check createInstance(CheckController checkController, Sequence sequence, User user) {
            return null;
        }
    }

}
