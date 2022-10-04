package com.guflimc.brick.regions.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.regions.api.rules.RuleStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class RuleStatusArgument<C> extends CommandArgument<C, RuleStatus> {

    private RuleStatusArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new RuleStatusParser<>(), defaultValue, RuleStatus.class, suggestionsProvider);
    }

    public static final class RuleStatusParser<C> implements ArgumentParser<C, RuleStatus> {

        @Override
        public @NotNull ArgumentParseResult<RuleStatus> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        RuleStatusParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            try {
                RuleStatus ruleStatus = RuleStatus.valueOf(input);
                return ArgumentParseResult.success(ruleStatus);
            } catch (IllegalArgumentException ex) {
                return ArgumentParseResult.failure(new RuleStatusParseException(input, commandContext));
            }
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();
            Arrays.stream(RuleStatus.values()).forEach(rt -> output.add(rt.name()));
            return output;
        }

    }

    public static final class RuleStatusParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public RuleStatusParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    RuleStatusArgument.RuleStatusParser.class,
                    context,
                    Caption.of("cmd.args.error.rulestatus"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}