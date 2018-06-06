package ru.alexanderdv.citysimulator;

import java.util.HashMap;

/**
 * 
 * @author AlexanderDV
 * @version 0.0.5a
 */
public class PointD
{
	private static final HashMap<Double, HashMap<Double, PointD>> points = new HashMap<Double, HashMap<Double, PointD>>();
	final double x, z;

	private PointD(double x, double z)
	{
		this.x = x;
		this.z = z;
	}

	public static PointD get(double x, double z)
	{
		if (points.get(x) != null)
			if (points.get(x).get(z) != null)
				return points.get(x).get(z);

		PointD p = new PointD(x, z);
		if (points.get(x) == null)
			points.put(x, new HashMap<Double, PointD>());
		points.get(x).put(z, p);
		return p;
	}

	@Override
	public String toString()
	{
		return super.toString()+"[x="+x+", z="+z+"]";
	}

}