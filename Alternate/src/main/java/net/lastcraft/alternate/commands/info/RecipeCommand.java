package net.lastcraft.alternate.commands.info;

import net.lastcraft.alternate.commands.AlternateCommand;
import net.lastcraft.alternate.config.ConfigData;
import net.lastcraft.alternate.object.CraftUser;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Version;
import net.lastcraft.base.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated //на 1.12 не нужно
public class RecipeCommand extends AlternateCommand {
    private final transient static Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    public RecipeCommand(ConfigData configData) {
        super(configData, true, "recipe", "formula","method","recipes");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();
        Language lang = gamer.getLanguage();

        int version = gamer.getVersion().getVersion();
        if (version >= Version.MINECRAFT_1_12.getVersion() || version == -1) {
            gamer.sendMessageLocale("RECIPE_DISABLED");
            return;
        }

        if (args.length < 1) {
            COMMANDS_API.notEnoughArguments(gamerEntity, "RECIPE_FORMAT");
            return;
        }

        ItemStack itemType;
        try {
            itemType = get(args[0]);
        } catch (Exception e) {
            gamer.sendMessageLocale("INVALID_ITEM_RECIPE");
            return;
        }
        int recipeNo = 0;

        if (args.length > 1) {
            if (isInt(args[1])) {
                recipeNo = Integer.parseInt(args[1]) - 1;
            } else {
                gamer.sendMessageLocale("INVALID_ITEM_RECIPE");
                return;
            }
        }

        final List<Recipe> recipesOfType = Bukkit.getServer().getRecipesFor(itemType);
        if (recipesOfType.size() < 1) {
            gamer.sendMessageLocale("INVALID_ITEM_RECIPE");
            return;
        }

        if (recipeNo < 0 || recipeNo >= recipesOfType.size()) {
            gamer.sendMessageLocale("INVALID_ITEM_RECIPE");
            return;
        }

        final Recipe selectedRecipe = recipesOfType.get(recipeNo);

        if (selectedRecipe instanceof FurnaceRecipe) {
            gamer.sendMessageLocale("RECIPE_FURNACE", getMaterialName(((FurnaceRecipe) selectedRecipe).getInput()));
        } else if (selectedRecipe instanceof ShapedRecipe) {
            shapedRecipe(player, (ShapedRecipe) selectedRecipe);
        } else if (selectedRecipe instanceof ShapelessRecipe) {
            if (recipesOfType.size() == 1 && itemType.getType() == Material.FIREWORK) {
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(itemType);
                shapelessRecipe.addIngredient(Material.SULPHUR);
                shapelessRecipe.addIngredient(Material.PAPER);
                shapelessRecipe.addIngredient(Material.FIREWORK_CHARGE);
                shapelessRecipe(player, shapelessRecipe);
            } else {
                shapelessRecipe(player, (ShapelessRecipe) selectedRecipe);
            }
        }

        if (recipesOfType.size() > 1 && args.length == 1) {
            player.sendMessage(configData.getPrefix() + lang.getMessage( "RECIPE_LIST_VIEW",
                    getMaterialName(itemType), s, args[0].toUpperCase()));
        }
    }

    @SuppressWarnings("deprecation")
    private ItemStack get(final String id) throws Exception{
        int itemid = 0;
        String itemname;
        short metaData = 0;
        Matcher parts = splitPattern.matcher(id);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemname = id;
        }

        if (isInt(itemname))
            itemid = Integer.parseInt(itemname);
        else if (isInt(id))
            itemid = Integer.parseInt(id);
        else
            itemname = itemname.toLowerCase(Locale.ENGLISH);


        if (itemid < 1) {
            if (Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH)) != null) {
                Material bMaterial = Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH));
                itemid = bMaterial.getId();
            } else {
                try {
                    Material bMaterial = Bukkit.getUnsafe()
                            .getMaterialFromInternalName(itemname.toLowerCase(Locale.ENGLISH));
                    itemid = bMaterial.getId();
                } catch (Throwable throwable) {
                    throw new Exception("unknownItemName" + itemname, throwable);
                }
            }
        }

        if (itemid < 1){
            throw new Exception("unknownItemName " + itemname);
        }

        final Material mat = Material.getMaterial(itemid);
        if (mat == null){
            throw new Exception("unknownItemId " + itemid);
        }
        final ItemStack retval = new ItemStack(mat);
        retval.setAmount(mat.getMaxStackSize());
        retval.setDurability(metaData);
        return retval;
    }

    private static boolean isInt(String id) {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void shapedRecipe(Player player, ShapedRecipe recipe) {
        final CraftUser craftUser = (CraftUser) USER_MANAGER.getUser(player);
        player.closeInventory();
        craftUser.setRecipeSee(true);
        InventoryView view = player.openWorkbench(null, true);
        final String[] recipeShape = recipe.getShape();
        final Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
        for (int j = 0; j < recipeShape.length; j++) {
            for (int k = 0; k < recipeShape[j].length(); k++) {
                final ItemStack item = ingredientMap.get(recipeShape[j].toCharArray()[k]);
                if (item == null)
                    continue;

                item.setAmount(1);
                view.getTopInventory().setItem(j * 3 + k + 1, item);
            }
        }
    }

    private void shapelessRecipe(Player player, ShapelessRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredientList();
        CraftUser craftUser = (CraftUser) USER_MANAGER.getUser(player);
        craftUser.setRecipeSee(true);
        InventoryView view = player.openWorkbench(null, true);

        for (int i = 0; i < ingredients.size(); i++)
            view.setItem(i + 1, ingredients.get(i));

    }

    private String getMaterialName(final ItemStack stack) {
        if (stack == null)
            return "recipeNothing";

        return getMaterialName(stack.getType());
    }

    private String getMaterialName(final Material type) {
        if (type == null)
            return "recipeNothing";

        return type.toString().replace("_", " ").toUpperCase(Locale.ENGLISH);
    }

}
