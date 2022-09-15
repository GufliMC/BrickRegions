package com.guflimc.brick.regions.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.regions.api.RegionAPI;
import com.guflimc.brick.regions.api.domain.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class RegionArgument<C> extends CommandArgument<C, Region> {

    private RegionArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new RegionParser<>(), defaultValue, Region.class, suggestionsProvider);
    }

    public static final class RegionParser<C> implements ArgumentParser<C, Region> {

        @Override
        public @NotNull ArgumentParseResult<Region> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        RegionParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Region region = RegionAPI.get().findRegion(input).orElse(null);

            if (region == null) {
                return ArgumentParseResult.failure(new RegionParseException(input, commandContext));
            }

            return ArgumentParseResult.success(region);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            RegionAPI.get().regions().stream().filter(rg -> rg.name() != null)
                    .forEach(rg -> output.add(rg.name()));

            return output;
        }

    }

    public static final class RegionParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public RegionParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    RegionArgument.RegionParser.class,
                    context,
                    Caption.of("cmd.args.error.Region"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}