package ruiseki.integrateddynamics.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;

/**
 * An effect writer part.
 *
 * @author rubensworks
 */
public class PartTypeEffectWriter
    extends PartTypeWriteBase<PartTypeEffectWriter, PartStateWriterBase<PartTypeEffectWriter>> {

    public PartTypeEffectWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    Aspects.Write.Effect.createForParticle("fireworksSpark"),
                    Aspects.Write.Effect.createForParticle("bubble"),
                    Aspects.Write.Effect.createForParticle("splash"),
                    Aspects.Write.Effect.createForParticle("wake"),
                    Aspects.Write.Effect.createForParticle("suspended"),
                    Aspects.Write.Effect.createForParticle("depthsuspend"),
                    Aspects.Write.Effect.createForParticle("crit"),
                    Aspects.Write.Effect.createForParticle("magicCrit"),
                    Aspects.Write.Effect.createForParticle("smoke"),
                    Aspects.Write.Effect.createForParticle("largesmoke"),
                    Aspects.Write.Effect.createForParticle("spell"),
                    Aspects.Write.Effect.createForParticle("instantSpell"),
                    Aspects.Write.Effect.createForParticle("mobSpell"),
                    Aspects.Write.Effect.createForParticle("mobSpellAmbient"),
                    Aspects.Write.Effect.createForParticle("witchMagic"),
                    Aspects.Write.Effect.createForParticle("dripWater"),
                    Aspects.Write.Effect.createForParticle("dripLava"),
                    Aspects.Write.Effect.createForParticle("angryVillager"),
                    Aspects.Write.Effect.createForParticle("happyVillager"),
                    Aspects.Write.Effect.createForParticle("townaura"),
                    Aspects.Write.Effect.createForParticle("note"),
                    Aspects.Write.Effect.createForParticle("portal"),
                    Aspects.Write.Effect.createForParticle("enchantmenttable"),
                    Aspects.Write.Effect.createForParticle("flame"),
                    Aspects.Write.Effect.createForParticle("lava"),
                    Aspects.Write.Effect.createForParticle("footstep"),
                    Aspects.Write.Effect.createForParticle("cloud"),
                    Aspects.Write.Effect.createForParticle("reddust"),
                    Aspects.Write.Effect.createForParticle("snowballpoof"),
                    Aspects.Write.Effect.createForParticle("snowshovel"),
                    Aspects.Write.Effect.createForParticle("slime"),
                    Aspects.Write.Effect.createForParticle("heart")));
    }

    @Override
    public PartStateWriterBase<PartTypeEffectWriter> constructDefaultState() {
        return new PartStateWriterBase<PartTypeEffectWriter>(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

}
