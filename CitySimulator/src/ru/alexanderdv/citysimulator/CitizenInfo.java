package ru.alexanderdv.citysimulator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author AlexanderDV
 * @version 0.0.5a
 */
public class CitizenInfo
{
	public final ArrayList<Long> children = new ArrayList<Long>();
	public final HashMap<DNA, Double> fetuses = new HashMap<DNA, Double>();
	String surname;
	String name;
	String secondName;
	Long pair =-1l;

	@Override
	public String toString()
	{
		return "CitizenInfo [children=" + children + ", fetuses=" + fetuses + ", pair=" + pair + "]";
	}
}

