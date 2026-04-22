package com.arf8vhg7.jja.feature.combat.strike.client;

import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeParticleOptions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.jujutsucraft.entity.EntityAttackStrikeEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModAttributes;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.phys.Vec3;

public final class JjaAttackStrikeMatchParticle extends Particle {
    private static final String SLASH_WHITE_TEXTURE = "slash_white";
    private static final int VISIBLE_LIFETIME_TICKS = 5;

    private final EntityAttackStrikeEntity renderProxy;
    private final float renderScale;
    private final float yawDegrees;
    private final float pitchDegrees;

    public JjaAttackStrikeMatchParticle(
        JjaAttackStrikeParticleOptions options,
        ClientLevel level,
        double x,
        double y,
        double z,
        double xSpeed,
        double ySpeed,
        double zSpeed
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.renderScale = options.renderScale();
        this.yawDegrees = options.yawDegrees();
        this.pitchDegrees = options.pitchDegrees();
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.lifetime = VISIBLE_LIFETIME_TICKS;
        this.renderProxy = new EntityAttackStrikeEntity((EntityType<EntityAttackStrikeEntity>) JujutsucraftModEntities.ENTITY_ATTACK_STRIKE.get(), level);
        this.renderProxy.setTexture(SLASH_WHITE_TEXTURE);
        this.renderProxy.setAnimation(options.animation().animationName());
        updateRenderProxy();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        updateRenderProxy();
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        Vec3 cameraPosition = camera.getPosition();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        PoseStack poseStack = new PoseStack();

        updateRenderProxy();
        dispatcher.render(
            this.renderProxy,
            this.x - cameraPosition.x,
            this.y - cameraPosition.y,
            this.z - cameraPosition.z,
            this.renderProxy.getYRot(),
            partialTick,
            poseStack,
            bufferSource,
            this.getLightColor(partialTick)
        );
        bufferSource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    private void updateRenderProxy() {
        this.renderProxy.tickCount = this.age;
        this.renderProxy.setPos(this.x, this.y, this.z);
        this.renderProxy.xo = this.x;
        this.renderProxy.yo = this.y;
        this.renderProxy.zo = this.z;
        this.renderProxy.setYRot(this.yawDegrees);
        this.renderProxy.setXRot(this.pitchDegrees);
        this.renderProxy.yRotO = this.yawDegrees;
        this.renderProxy.xRotO = this.pitchDegrees;
        this.renderProxy.yBodyRot = this.yawDegrees;
        this.renderProxy.yBodyRotO = this.yawDegrees;
        this.renderProxy.yHeadRot = this.yawDegrees;
        this.renderProxy.yHeadRotO = this.yawDegrees;

        AttributeInstance size = this.renderProxy.getAttribute(JujutsucraftModAttributes.SIZE.get());
        if (size != null) {
            size.setBaseValue(this.renderScale);
        }
    }
}
