package com.guflimc.brick.regions.common.commands;

import com.guflimc.brick.regions.api.domain.LocalityAttributeKey;

public class RegionAttributeCommands {

    private static final LocalityAttributeKey<?>[] KEYS = new LocalityAttributeKey[]{
            LocalityAttributeKey.MAP_HIDDEN,
            LocalityAttributeKey.MAP_CLICK_TEXT,
            LocalityAttributeKey.MAP_HOVER_TEXT,
            LocalityAttributeKey.MAP_FILL_COLOR,
            LocalityAttributeKey.MAP_FILL_OPACITY,
            LocalityAttributeKey.MAP_STROKE_COLOR,
            LocalityAttributeKey.MAP_STROKE_OPACITY,
            LocalityAttributeKey.MAP_STROKE_WEIGHT,
    };

    public RegionAttributeCommands() {
    }

//    public static <S> void register(CommandManager<S> commandManager) {
//        for (LocalityAttributeKey<?> attribute : KEYS) {
//            register(commandManager, attribute);
//        }
//    }
//
//    private static <T, S> void register(CommandManager<S> commandManager, LocalityAttributeKey<T> attribute) {
//        // for regions
//        commandManager.command(Command.<S>newBuilder(
//                        "attribute_" + attribute.name(), CommandMeta.simple().build())
//                .literal("br")
//                .literal("attribute")
//                .literal(attribute.name())
//                .argument(CommandArgument.ofType(Region.class, "region"))
//                .argument(CommandArgument.ofType(attribute.type(), "value"))
//                .handler(ctx -> {
//                    Audience sender = ctx.get("audience");
//                    Region region = ctx.get("region");
//                    ModifiableAttributedLocality mr = ctx.get("region");
//                    T value = ctx.get("value");
//
//                    mr.setAttribute(attribute, value);
//                    I18nAPI.get(RegionAttributeCommands.class)
//                            .send(sender, "cmd.region.attribute", region.name(), attribute.name(), value.toString());
//                })
//        );
//
//        // for tiles
//        commandManager.command(Command.<S>newBuilder(
//                        "attribute_" + attribute.name(), CommandMeta.simple().build())
//                .literal("br")
//                .literal("tile")
//                .literal("attribute")
//                .literal(attribute.name())
//                .argument(CommandArgument.ofType(attribute.type(), "value"))
//                .handler(ctx -> {
//                    Audience sender = ctx.get("audience");
//                    ModifiableAttributedLocality tile = ctx.get("tile");
//                    T value = ctx.get("value");
//
//                    tile.setAttribute(attribute, value);
//                    I18nAPI.get(RegionAttributeCommands.class)
//                            .send(sender, "cmd.tile.attribute", attribute.name(), value.toString());
//                })
//        );
//    }



    //

//    @Suggestions("attribute")
//    public List<String> attributeSuggestions(CommandContext<?> context, String input) {
//        return Arrays.stream(KEYS)
//                .map(LocalityAttributeKey::name)
//                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
//                .collect(Collectors.toList());
//    }
//
//    @Parser(name = "attribute", suggestions = "attribute")
//    public LocalityAttributeKey<?> attribute(CommandContext<?> context, Queue<String> queue) {
//        String input = queue.peek();
//        if ( input == null ) return null;
//        return Arrays.stream(KEYS)
//                .filter(s -> s.name().equalsIgnoreCase(input))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Unknown attribute: " + input));
//    }
//
//    @Command("br attribute set <region> <attribute> <value>")
//    @CommandPermission("brick.attribute.set")
//    public <T, R extends Region & ModifiableAttributedLocality> void attributeSet(Audience sender,
//                             @Argument(value = "region", parserName = "region") R region,
//                             @Argument(value = "attribute", parserName = "attribute") LocalityAttributeKey<T> attribute,
//                             @Argument(value = "value") String value) {
//        T parsed;
//        try {
//            parsed = attribute.deserialize(value);
//        } catch (Exception e) {
//            I18nAPI.get(this).send(sender, "cmd.region.attribute.set.error.parse", attribute.name(), value);
//            return;
//        }
//
//        region.setAttribute(attribute, parsed);
//        I18nAPI.get(this).send(sender, "cmd.region.attribute.set", region.name(), attribute.name(), value);
//    }
}
