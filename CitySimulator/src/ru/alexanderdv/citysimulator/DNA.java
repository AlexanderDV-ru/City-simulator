package ru.alexanderdv.citysimulator;

public class DNA
{
	public static final int size = 100;
	int[] nucl;

	public DNA(int[] nucl)
	{
		super();
		this.nucl = nucl;
	}

	public static DNA random()
	{
		int[] nucl = new int[size];
		for (int i = 0; i < nucl.length; i++)
			nucl[i] = Main.random.nextInt(4);
		return new DNA(nucl);
	}
}