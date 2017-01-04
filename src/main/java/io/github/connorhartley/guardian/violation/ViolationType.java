/*
 * MIT License
 *
 * Copyright (c) 2016 Connor Hartley
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
package io.github.connorhartley.guardian.violation;

import io.github.connorhartley.guardian.util.StabilityStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

/**
 * Represents violation
 */
public interface ViolationType {

    void onConstruction();

    void onDeconstruction();

    boolean isReady();

    StabilityStatus getStabilityStatus();

    void handleConnect(Player player);

    void handleConnect(User user);

    void handleDisconnect(Player player);

    void handleDisconnect(User user);

    Check<?, ?>[] getChecks();

    interface Check<P, E> {

        void start();

        void update();

        void stop();

        boolean isExecuting();

        P getProvider();

        E getEntity();

    }

}