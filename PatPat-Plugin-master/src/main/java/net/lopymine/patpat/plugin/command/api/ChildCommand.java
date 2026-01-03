package net.lopymine.patpat.plugin.command.api;

import lombok.*;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ChildCommand {

	@NotNull
	private SimpleCommand command;
	@NotNull
	private String name;
	private String[] aliases;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChildCommand childCommand = (ChildCommand) o;
		return command == childCommand.command && name.equals(childCommand.name) && Arrays.equals(aliases, childCommand.aliases);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(command);
		result = 31 * result + Objects.hash(name);
		result = 31 * result + Arrays.hashCode(aliases);
		return result;
	}
}
