package random.bot.command;

import static net.jcraron.command.split.parse.ParserHelper.entryParserSupplier;
import static net.jcraron.command.split.parse.ParserHelper.intParserSupplier;
import static net.jcraron.command.split.parse.ParserHelper.listParserSupplier;
import static net.jcraron.command.split.parse.ParserHelper.normalParserSupplier;
import static random.bot.command.CommandLoader.register;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import net.jcraron.command.Command;
import net.jcraron.command.CommandReturn;
import net.jcraron.command.MultiExecuteCommand;
import net.jcraron.command.split.parse.Parser;
import net.jcraron.table.ArrayTable;
import net.jcraron.table.Table;
import random.bot.RandomHelper;
import random.bot.RandomHelper.Counter;

public class CommandInstances {
	static {
		Function<String, Parser> normalListParser = listParserSupplier(normalParserSupplier());
		Function<String, Parser> entryListParser = listParserSupplier(
				entryParserSupplier(normalParserSupplier(), intParserSupplier()));
		Command random = new MultiExecuteCommand("random")
				.newBuilder().appendSupplier(entryListParser).build(CommandInstances::randomWeights_run)
				.newBuilder().appendSupplier(normalListParser).build(CommandInstances::random_run);
		Command shuffle = new MultiExecuteCommand("shuffle")
				.newBuilder().appendSupplier(normalListParser).build(CommandInstances::shuffle_run);
		Command show = new MultiExecuteCommand("show")
				.newBuilder().build((returns, arg) -> returns.tryBuildTableMessage());

		register(random);
		register(shuffle);
		register(show);
	}

	static void random_run(CommandReturn returns, List<Parser> arg) {
		List<String> list = arg.get(0).unbox();
		int randInt = RandomHelper.getRandom().nextInt(list.size());
		String randString = list.get(randInt);
		returns.setMessage(randString);
	}

	// TODO Tree
	// newBuilder.appendSupplier.appendSupplier.push().appendSupplier.setExecute.pop().appendSupplier.setExecute.build
	// TODO ArgParser parse(tree)
	// TODO tree=nextNode();nextLayer();getExecute();
	// TODO arrayTree=nextLayer();getExecute();

	static void randomWeights_run(CommandReturn returns, List<Parser> arg) {
		List<Entry<String, Integer>> entryList = arg.get(0).unbox();
		Counter[] counters = RandomHelper.weightsCount(entryList, 1);
		Table table = returns.getTable();
		table.addAll(counters);
		Table sub = table.subTable(ArrayTable::new, (i, row) -> row instanceof Counter).sort((row1,
				row2) -> -Integer.compare((int) row1.get(Counter.COUNTS_KEY), (int) row2.get(Counter.COUNTS_KEY)));
		returns.setMessage(sub.getRow(0).get(Counter.NAME_KEY).toString());
	}

	static void shuffle_run(CommandReturn returns, List<Parser> arg) {
		List<String> list = arg.get(0).unbox();
		Collections.shuffle(list, RandomHelper.getRandom());
		returns.setMessage(list.toString());
	}
}
