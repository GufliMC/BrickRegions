package com.guflimc.brick.regions.common.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.regions.api.rules.RuleTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class RuleTargetArgument<C> extends CommandArgument<C, RuleTarget> {

    private RuleTargetArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new RuleTargetParser<>(), defaultValue, RuleTarget.class, suggestionsProvider);
    }

    public static final class RuleTargetParser<C> implements ArgumentParser<C, RuleTarget> {

        @Override
        public @NotNull ArgumentParseResult<RuleTarget> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        RuleTargetParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            RuleTarget ruleType = RuleTarget.valueOf(input);

            if (ruleType == null) {
                return ArgumentParseResult.failure(new RuleTargetParseException(input, commandContext));
            }

            return ArgumentParseResult.success(ruleType);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();
            Arrays.stream(RuleTarget.values()).forEach(rt -> output.add(rt.name()));
            return output;
        }

    }

    public static final class RuleTargetParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public RuleTargetParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    RuleTargetArgument.RuleTargetParser.class,
                    context,
                    Caption.of("cmd.args.error.ruletarget"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}