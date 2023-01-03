package biden.tutorialmod.integration;

import java.util.List;
import java.util.Objects;

import biden.tutorialmod.TutorialMod;
import biden.tutorialmod.recipe.GemInfusingStationRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * JEITutorialModPlugin
 */
@JeiPlugin
public class JEITutorialModPlugin implements IModPlugin {
    public static RecipeType<GemInfusingStationRecipe> INFUSION_TYPE = new RecipeType<>(
            GemInfusingStationRecipeCategory.UID, GemInfusingStationRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TutorialMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration
                .addRecipeCategories(new GemInfusingStationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        RecipeManager rm = Objects.requireNonNull(minecraft.level).getRecipeManager();

        List<GemInfusingStationRecipe> recipesInfusing = rm.getAllRecipesFor(GemInfusingStationRecipe.Type.INSTANCE);
        registration.addRecipes(INFUSION_TYPE, recipesInfusing);
    }
}
