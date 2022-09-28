package com.jediterm.terminal.model.hyperlinks;

import com.jediterm.terminal.ui.TerminalAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author traff
 */
public class LinkInfo {
    private final Runnable myNavigateCallback;
    private final PopupMenuGroupProvider myPopupMenuGroupProvider;
    private final HoverConsumer myHoverConsumer;

    public LinkInfo(@NotNull Runnable navigateCallback) {
        this(navigateCallback, null, null);
    }

    private LinkInfo(@NotNull Runnable navigateCallback,
                     @Nullable PopupMenuGroupProvider popupMenuGroupProvider,
                     @Nullable com.jediterm.terminal.model.hyperlinks.LinkInfo.HoverConsumer hoverConsumer) {
        myNavigateCallback = navigateCallback;
        myPopupMenuGroupProvider = popupMenuGroupProvider;
        myHoverConsumer = hoverConsumer;
    }

    public void navigate() {
        myNavigateCallback.run();
    }

    public @Nullable PopupMenuGroupProvider getPopupMenuGroupProvider() {
        return myPopupMenuGroupProvider;
    }

    public @Nullable com.jediterm.terminal.model.hyperlinks.LinkInfo.HoverConsumer getHoverConsumer() {
        return myHoverConsumer;
    }

    public interface PopupMenuGroupProvider {
        @NotNull List<TerminalAction> getPopupMenuGroup(@NotNull MouseEvent event);
    }

    public interface HoverConsumer {
        /**
         * Gets called when the mouse cursor enters the link's bounds.
         *
         * @param hostComponent com.jediterm.terminal/console component containing the link
         * @param linkBounds    link's bounds relative to {@code hostComponent}
         */
        void onMouseEntered(@NotNull JComponent hostComponent, @NotNull Rectangle linkBounds);

        /**
         * Gets called when the mouse cursor exits the link's bounds.
         */
        void onMouseExited();
    }

    public static final class Builder {
        private Runnable myNavigateCallback;
        private PopupMenuGroupProvider myPopupMenuGroupProvider;
        private HoverConsumer myHoverConsumer;

        public @NotNull Builder setNavigateCallback(@NotNull Runnable navigateCallback) {
            myNavigateCallback = navigateCallback;
            return this;
        }

        public @NotNull Builder setPopupMenuGroupProvider(@Nullable PopupMenuGroupProvider popupMenuGroupProvider) {
            myPopupMenuGroupProvider = popupMenuGroupProvider;
            return this;
        }

        public @NotNull Builder setHoverConsumer(@Nullable com.jediterm.terminal.model.hyperlinks.LinkInfo.HoverConsumer hoverConsumer) {
            myHoverConsumer = hoverConsumer;
            return this;
        }

        public @NotNull com.jediterm.terminal.model.hyperlinks.LinkInfo build() {
            return new com.jediterm.terminal.model.hyperlinks.LinkInfo(myNavigateCallback, myPopupMenuGroupProvider, myHoverConsumer);
        }
    }
}
