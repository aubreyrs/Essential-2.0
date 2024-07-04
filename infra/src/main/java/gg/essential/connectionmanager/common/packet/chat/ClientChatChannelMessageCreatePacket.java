/*
 * Copyright (c) 2024 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.'s Essential Mod repository and is protected
 * under copyright registration # TX0009138511. For the full license, see:
 * https://github.com/EssentialGG/Essential/blob/main/LICENSE
 *
 * You may not use, copy, reproduce, modify, sell, license, distribute,
 * commercialize, or otherwise exploit, or create derivative works based
 * upon, this file or any other in this repository, all of which is reserved by Essential.
 */
package gg.essential.connectionmanager.common.packet.chat;

import gg.essential.lib.gson.annotations.SerializedName;
import gg.essential.connectionmanager.common.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientChatChannelMessageCreatePacket extends Packet {

    @SerializedName("a")
    private final long channelId;

    @SerializedName("b")
    @NotNull
    private final String content;

    @SerializedName("c")
    @Nullable
    private final Long replyTargetId;

    public ClientChatChannelMessageCreatePacket(
            final long channelId,
            final @NotNull String content
    ) {
        this.channelId = channelId;
        this.content = content;
        this.replyTargetId = null;
    }

    public ClientChatChannelMessageCreatePacket(
            final long channelId,
            final @NotNull String content,
            final @Nullable Long replyTargetId
    ) {
        this.channelId = channelId;
        this.content = content;
        this.replyTargetId = replyTargetId;
    }

    public long getChannelId() {
        return channelId;
    }

    @NotNull
    public String getContent() {
        return this.content;
    }

    @Nullable
    public Long getReplyTargetId() {
        return replyTargetId;
    }

}