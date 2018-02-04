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
package com.ichorpowered.guardian.common.check.movement;

import com.abilityapi.sequenceapi.SequenceBlueprint;
import com.abilityapi.sequenceapi.SequenceContext;
import com.abilityapi.sequenceapi.action.condition.ConditionType;
import com.google.common.collect.Sets;
import com.ichorpowered.guardian.common.capture.PlayerControlCapture;
import com.ichorpowered.guardian.entry.GuardianPlayerEntry;
import com.ichorpowered.guardian.sequence.GuardianSequence;
import com.ichorpowered.guardian.sequence.GuardianSequenceBuilder;
import com.ichorpowered.guardian.sequence.SequenceReport;
import com.ichorpowered.guardian.sequence.capture.GuardianCaptureRegistry;
import com.ichorpowered.guardian.sequence.context.CommonContextKeys;
import com.ichorpowered.guardian.util.item.mutable.GuardianValue;
import com.ichorpowered.guardianapi.content.ContentKeys;
import com.ichorpowered.guardianapi.detection.Detection;
import com.ichorpowered.guardianapi.detection.capture.CaptureContainer;
import com.ichorpowered.guardianapi.detection.check.Check;
import com.ichorpowered.guardianapi.detection.report.Summary;
import com.ichorpowered.guardianapi.event.origin.Origin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.Location;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

public class InvalidCheck implements Check<Event> {

    public InvalidCheck() {}

    @Override
    public String getId() {
        return "guardian:invalidcheck";
    }

    @Override
    public String getName() {
        return "Invalid Movement Check";
    }

    @Override
    public Set<String> getTags() {
        return Sets.newHashSet(
                "guardian",
                "internal",
                "movement",
                "invalidmovement"
        );
    }

    @Nonnull
    @Override
    public SequenceBlueprint<Event> getSequence(final Detection detection) {
        final Double analysisTime = detection.getContentContainer().get(ContentKeys.ANALYSIS_TIME).orElse(GuardianValue.empty())
                .getDirect().orElse(0d) / 0.05;

        final Double minimumTickRate = detection.getContentContainer().get(ContentKeys.ANALYSIS_MINIMUM_TICK).orElse(GuardianValue.empty())
                .getDirect().orElse(0d) * analysisTime;

        final Double maximumTickRate = detection.getContentContainer().get(ContentKeys.ANALYSIS_MAXIMUM_TICK).orElse(GuardianValue.empty())
                .getDirect().orElse(0d) * analysisTime;

        return new GuardianSequenceBuilder()

                .capture(new PlayerControlCapture.Invalid(detection.getPlugin(), detection))

                // Observe : Move Entity Event

                .observe(MoveEntityEvent.class)

                // Observe : Move Entity Event

                .observe(MoveEntityEvent.class)
                    .delay(analysisTime.intValue())
                    .expire(maximumTickRate.intValue())

                    // TODO: Permission check.

                    .condition(sequenceContext -> {
                        final GuardianPlayerEntry<Player> entityEntry = sequenceContext.get(CommonContextKeys.ENTITY_ENTRY);
                        final Summary summary = sequenceContext.get(CommonContextKeys.SUMMARY);
                        final GuardianCaptureRegistry captureRegistry = sequenceContext.get(CommonContextKeys.CAPTURE_REGISTRY);
                        final long lastActionTime = sequenceContext.get(CommonContextKeys.LAST_ACTION_TIME);

                        summary.set(SequenceReport.class, new SequenceReport(false, Origin.source(sequenceContext.getRoot()).owner(entityEntry).build()));

                        if (!entityEntry.getEntity(Player.class).isPresent()) return false;
                        final Player player = entityEntry.getEntity(Player.class).get();

                        /*
                         * Capture Collection
                         */

                        final CaptureContainer captureContainer = captureRegistry.getContainer();

                        Optional<Location> initial = captureContainer.get(GuardianSequence.INITIAL_LOCATION);
                        Optional<Set<String>> invalidControls = captureContainer.get(PlayerControlCapture.Invalid.INVALID_CONTROLS);

                        /*
                         * Analysis
                         */

                        if (!initial.isPresent() || !invalidControls.isPresent()) return false;

                        long current = System.currentTimeMillis();

                        // Finds the average between now and the last action.
                        double averageClockRate = ((current - lastActionTime) / 1000) / 0.05;

                        if (averageClockRate < minimumTickRate) {
                            detection.getLogger().warn("The server may be overloaded. A check could not be completed.");
                            return false;
                        } else if (averageClockRate > maximumTickRate) {
                            return false;
                        }

                        if (invalidControls.get().isEmpty()
                                || player.get(Keys.VEHICLE).isPresent()) return false;

                        // ------------------------- DEBUG -----------------------------
                        System.out.println(player.getName() + " has been caught using invalid movement hacks.");
                        // -------------------------------------------------------------

                        SequenceReport report = new SequenceReport(true, Origin.source(sequenceContext.getRoot()).owner(entityEntry).build());
                        report.put("type", "Invalid Movement");

                        report.put("information", Collections.singletonList(
                                "Received invalid controls of " + StringUtils.join((Set<String>) invalidControls.get(), ", ") + ".")
                        );

                        report.put("initial_location", initial.get());
                        report.put("final_location", player.getLocation());
                        report.put("severity", 1d);

                        summary.set(SequenceReport.class, report);

                        return true;
                    }, ConditionType.NORMAL)

                .build(SequenceContext.builder()
                        .owner(detection)
                        .root(this)
                        .build());
    }

    @Override
    public Class<? extends Event> getSequenceTrigger() {
        return MoveEntityEvent.class;
    }
}
