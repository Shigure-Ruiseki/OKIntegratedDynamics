package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorRegistry;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperatorSerializer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IOperatorVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.item.OperatorVariableFacade;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Registry for {@link IOperator}
 *
 * @author rubensworks
 */
public class OperatorRegistry implements IOperatorRegistry {

    private static OperatorRegistry INSTANCE = new OperatorRegistry();
    private static final IOperatorVariableFacade INVALID_FACADE = new OperatorVariableFacade(false, null, null);

    private final List<IOperator> operators = Lists.newArrayList();
    private final Map<String, IOperator> namedOperators = Maps.newHashMap();
    private final Multimap<List<IValueType>, IOperator> inputTypedOperators = HashMultimap.create();
    private final Multimap<IValueType, IOperator> outputTypedOperators = HashMultimap.create();
    private final Multimap<String, IOperator> categoryOperators = HashMultimap.create();
    private final List<IOperatorSerializer> serializers = Lists.newArrayList();
    private final Map<String, IOperatorSerializer> namedSerializers = Maps.newHashMap();
    private final IOperatorSerializer DEFAULT_SERIALIZER = new OperatorSerializerDefault();

    private OperatorRegistry() {
        if (MinecraftHelpers.isModdedEnvironment()) {
            IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class)
                .registerHandler(this);
        }
    }

    /**
     * @return The unique instance.
     */
    public static OperatorRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <O extends IOperator> O register(O operator) {
        operators.add(operator);
        namedOperators.put(
            operator.getUniqueName()
                .toString(),
            operator);
        inputTypedOperators.put(ImmutableList.copyOf(operator.getInputTypes()), operator);
        outputTypedOperators.put(operator.getOutputType(), operator);
        categoryOperators.put(operator.getUnlocalizedCategoryName(), operator);
        return operator;
    }

    @Override
    public Collection<IOperator> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    @Override
    public IOperator getOperator(ResourceLocation uniqueName) {
        return namedOperators.get(uniqueName.toString());
    }

    @Override
    public Collection<IOperator> getOperatorsWithInputTypes(IValueType... valueTypes) {
        return inputTypedOperators.get(ImmutableList.copyOf(valueTypes));
    }

    @Override
    public Collection<IOperator> getOperatorsWithOutputType(IValueType valueType) {
        return outputTypedOperators.get(valueType);
    }

    @Override
    public Collection<IOperator> getOperatorsInCategory(String categoryName) {
        return categoryOperators.get(categoryName);
    }

    @Override
    public void registerSerializer(IOperatorSerializer serializer) {
        serializers.add(serializer);
        namedSerializers.put(
            serializer.getUniqueName()
                .toString(),
            serializer);
    }

    @Override
    public NBTBase serialize(IOperator value) {
        NBTTagCompound tag = new NBTTagCompound();
        for (IOperatorSerializer serializer : serializers) {
            if (serializer.canHandle(value)) {
                tag.setString(
                    "serializer",
                    serializer.getUniqueName()
                        .toString());
                tag.setTag("value", serializer.serialize(value));
                return tag;
            }
        }
        return DEFAULT_SERIALIZER.serialize(value);
    }

    @Override
    public IOperator deserialize(NBTBase value) throws EvaluationException {
        if (value.getId() == Constants.NBT.TAG_COMPOUND) {
            NBTTagCompound tag = (NBTTagCompound) value;
            String serializerName = tag.getString("serializer");
            IOperatorSerializer serializer = namedSerializers.get(serializerName);
            if (serializer == null) {
                throw new EvaluationException(LangHelpers.localize(L10NValues.OPERATOR_ERROR_NO_DESERIALIZER, value));
            }
            return serializer.deserialize(tag.getTag("value"));
        }
        return DEFAULT_SERIALIZER.deserialize(value);
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "operator");
    }

    @Override
    public IOperatorVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("operatorName") || !tag.hasKey("variableIds")) {
            return INVALID_FACADE;
        }
        IOperator operator;
        try {
            operator = deserialize(tag.getCompoundTag("operatorName"));
        } catch (EvaluationException e) {
            return INVALID_FACADE;
        }
        if (operator == null) {
            return INVALID_FACADE;
        }
        int[] variableIds = tag.getIntArray("variableIds");
        return new OperatorVariableFacade(id, operator, variableIds);
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IOperatorVariableFacade variableFacade) {
        tag.setTag("operatorName", serialize(variableFacade.getOperator()));
        tag.setIntArray("variableIds", variableFacade.getVariableIds());
    }
}
