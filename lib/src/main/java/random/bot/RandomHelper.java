package random.bot;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.jcraron.table.TableRow;

public class RandomHelper {
	private static Random rand = new Random();

	public static Random getRandom() {
		return rand;
	}

	public static Counter[] randomCount(String[] objs, int totalCount) {
		int length = objs.length;
		Counter[] entries = new Counter[objs.length];
		for (int i = 0; i < objs.length; i++) {
			entries[i] = new Counter(objs[i]);
		}
		for (int times = 0; times < totalCount; times++) {
			int index = getRandom().nextInt() % length;
			entries[index].touch();
		}
		return entries;
	}

	public static Counter[] weightsCount(List<Entry<String, Integer>> entryList, int totalCount) {
		int totalWeights = 0;
		Counter[] counters = new Counter[entryList.size()];
		for (int i = 0; i < entryList.size(); i++) {
			Entry<String, Integer> entry = entryList.get(i);
			counters[i] = new Counter(entry.getKey(), entry.getValue(), 0);
			totalWeights += entry.getValue();
		}

		for (int times = 0; times < totalCount; times++) {
			int randInt = RandomHelper.getRandom().nextInt(totalWeights);
			for (Counter counter : counters) {
				if (randInt < counter.getWeights()) {
					counter.touch();
					break;
				} else {
					randInt -= counter.getWeights();
				}
			}
		}
		return counters;
	}

	public static Counter[] findMax(Counter[] counters) {
		Counter[] ret = new Counter[counters.length];
		ret[0] = counters[0];
		int len = 1;
		for (int i = 1; i < counters.length; i++) {
			Counter counter = counters[i];
			if (ret[0].getCount() < counter.getCount()) {
				ret[0] = counter;
				len = 1;
			} else {
				ret[len] = counter;
				len++;
			}
		}
		return Arrays.copyOf(ret, len);
	}

	public static class Counter implements TableRow {
		private String obj;
		private int count;
		private int weights;

		public final static String COUNTS_KEY = "COUNTS";
		public final static String WEIGHTS_KEY = "WEIGHTS";

		public Counter(String obj, int weights, int initCount) {
			this.obj = obj;
			this.weights = weights;
			this.count = initCount;
		}

		public Counter(String obj) {
			this(obj, 0, 1);
		}

		public Object getObj() {
			return obj;
		}

		public int getCount() {
			return count;
		}

		public int getWeights() {
			return weights;
		}

		public void touch() {
			count++;
		}

		@Override
		public Object get(String key) {
			return switch (key) {
			case NAME_KEY -> obj;
			case COUNTS_KEY -> count;
			case WEIGHTS_KEY -> weights;
			default -> null;
			};
		}

		@Override
		public String[] keys() {
			return new String[] { NAME_KEY, WEIGHTS_KEY, COUNTS_KEY };
		}

		@Override
		public TableRow copy() {
			return new Counter(obj, weights, count);
		}
	}
}
