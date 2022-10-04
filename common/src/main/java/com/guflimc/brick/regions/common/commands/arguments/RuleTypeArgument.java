package com.guflimc.brick.RuleTypes.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.regions.api.rules.RuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class RuleTypeArgument<C> extends CommandArgument<C, RuleType> {

    private RuleTypeArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new RuleTypeParser<>(), defaultValue, RuleType.class, suggestionsProvider);
    }

    public static final class RuleTypeParser<C> implements ArgumentParser<C, RuleType> {

        @Override
        public @NotNull ArgumentParseResult<RuleType> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        RuleTypeParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            RuleType ruleType = RuleType.valueOf(input);

            if (ruleType == null) {
                return ArgumentParseResult.failure(new RuleTypeParseException(input, commandContext));
            }

            return ArgumentParseResult.success(ruleType);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();
            Arrays.stream(RuleType.values()).forEach(rt -> output.add(rt.name()));
            return output;
        }

    }

    public static final class RuleTypeParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public RuleTypeParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    RuleTypeArgument.RuleTypeParser.class,
                    context,
                    Caption.of("cmd.args.error.ruletype"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}