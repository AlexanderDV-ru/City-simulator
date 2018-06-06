package ru.alexanderdv.citysimulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Properties;

public class Citizen
{
	public final Long id;
	public final Long bioMather, bioFather;
	public final DNA dna;
	public final double born;
	public final CitizenProperties bornProps;
	public double x, y, z;
	public Point[] curPath;
	public final CitizenInfo info;
	public CitizenProperties curProps;
	public double death = -1;

	public Citizen(long id, long bioMather, long bioFather, DNA dna, double born)
	{
		this.id = id;
		this.bioMather = bioMather;
		this.bioFather = bioFather;
		this.dna = dna;
		this.born = born;
		this.bornProps = CitizenProperties.genByDNA(dna);
		this.curProps = bornProps;
		this.info = new CitizenInfo();

		 this.info.surname = Main.getSurname("ru", curProps.physicalGender);
		this.info.name = Main.getName("ru", curProps.physicalGender);

		rightLeg = new Leg();
		leftLeg = new Leg();
		rightArm = new Arm();
		leftArm = new Arm();
		head = new Head();
		neck = new Neck();
		stomach = new Stomach();
		chest = new Chest();

		leftEye = new Eye();
		rightEye = new Eye();
		leftEar = new Ear();
		rightEar = new Ear();
		nose = new Nose();
		mouth = new MouthThroatAndTongue();
		heart = new Heart();
		lungs = new Lungs();
		kidneys = new Kidneys();
		liver = new Liver();
	}

	public double getAge()
	{
		return (Main.time - born) / (Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay * Main.daysInMonth
				* Main.monthsInSeason * Main.seasonsInYear);
	}

	public boolean canWalk()
	{
		return getAge() >= walkAge && (rightLeg != null ? rightLeg.isNotIll() : false)
				&& (leftLeg != null ? leftLeg.isNotIll() : false);
	}

	Leg rightLeg, leftLeg;
	Arm rightArm, leftArm;
	Head head;
	Neck neck;
	Stomach stomach;
	Chest chest;

	Eye leftEye, rightEye;
	Ear leftEar, rightEar;
	Nose nose;
	MouthThroatAndTongue mouth;
	Heart heart;
	Lungs lungs;
	Kidneys kidneys;
	Liver liver;

	static abstract class PartOfOrganism
	{
		public abstract boolean isNotIll();
	}

	static abstract class Organ extends PartOfOrganism
	{
		boolean injury;
	}

	static class Eye extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Ear extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class MouthThroatAndTongue extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Nose extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Heart extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Lungs extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Liver extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static class Kidneys extends Organ
	{

		@Override
		public boolean isNotIll()
		{
			return !injury;
		}
	}

	static abstract class PartOfBody extends PartOfOrganism
	{
		boolean bleeding;
	}

	static abstract class Limb extends PartOfBody
	{
		boolean broken;

		@Override
		public boolean isNotIll()
		{
			return !broken && !bleeding;
		}
	}

	static class Leg extends Limb
	{
	}

	static class Arm extends Limb
	{
	}

	static class Neck extends PartOfBody
	{
		boolean broken;

		@Override
		public boolean isNotIll()
		{
			return !broken && !bleeding;
		}
	}

	static class Head extends PartOfBody
	{
		boolean concussion;

		@Override
		public boolean isNotIll()
		{
			return !concussion && !bleeding;
		}
	}

	static class Chest extends PartOfBody
	{
		boolean ribsBroken;

		@Override
		public boolean isNotIll()
		{
			return !ribsBroken && !bleeding;
		}
	}

	static class Stomach extends PartOfBody
	{
		@Override
		public boolean isNotIll()
		{
			return !bleeding;
		}
	}

	double walkAge = 0.8 + Main.random.nextInt(6) * 0.1;

	public String toDisplayLook()
	{
		String s = "Citizen [id=" + id + "\n";
		s += "bioMather=" + bioMather + "\n";
		s += "bioFather=" + bioFather + "\n";
		s += "dna=" + dna + "\n";
		s += "born=" + Main.toDisplayDate(born) + "\n";
		s += "bornProps=" + bornProps + "\n";
		s += "x=" + x + "\n";
		s += "y=" + y + "\n";
		s += "z=" + z + "\n";
		s += "info=" + info + "\n";
		s += "curProps=" + curProps + "\n";
		s += "death=" + Main.toDisplayDate(death) + "\n";
		s += "fertileAge=" + fertileAge + "\n";
		s += "defertileAge=" + defertileAge + "\n";
		s += "canBeFertile=" + canBeFertile + "\n";
		s += "fertile=" + fertile + "\n";
		s += "deathCause=" + deathCause + "\n";
		s += "timeOfOld=" + timeOfOld + "\n";
		s += "age=" + Main.toDisplayDate((isDead() ? death : Main.time) - born) + "\n";
		s += "time=" + Main.toDisplayDate(Main.time);
		return s;
	}

	public boolean isDead()
	{
		return death != -1;
	}

	int surnameCoef = Main.random.nextInt(10) - 4;

	public ArrayList<AbstractCitizen> tryToBorn()
	{
		if (curProps.physicalGender != Gender.Woman)
			return null;
		ArrayList<AbstractCitizen> citizens = new ArrayList<AbstractCitizen>();
		if (info.fetuses.size() > 0)
		{
			if (info.fetuses.values().toArray(new Double[0])[0] > Main.secondsInMinute * Main.minutesInHour
					* Main.hoursInDay * Main.daysInMonth * Main.monthsInSeason * Main.seasonsInYear * 0.8)
			{
				for (DNA dna : info.fetuses.keySet())
				{
					Citizen ctz = new Citizen(Main.ins.citizens.size() + citizens.size(), id, info.pair, dna,
							Main.time);
					String pairSurname = Main.ins.citizens.get(info.pair).citizen.info.surname;
					int coef2 = surnameCoef + Main.ins.citizens.get(info.pair).citizen.surnameCoef+2;
					if (coef2 < -1)
						ctz.info.surname = info.surname;
					else if (coef2 != -1 && coef2 != 0 && coef2 != +1)
						ctz.info.surname = pairSurname;
					if (coef2 == 0)
						if (!pairSurname.contains("-") && !info.surname.contains("-"))
							ctz.info.surname = info.surname + "-" + pairSurname;
						else coef2 = Main.random.nextInt(2) == 0 ? -1 : +1;
					if (coef2 == -1)
						ctz.info.surname = info.surname;
					else if (coef2 == +1)
						ctz.info.surname = pairSurname;

					citizens.add(new AbstractCitizen(ctz));
				}
				info.fetuses.clear();
			}
			for (AbstractCitizen AbstractCitizen : citizens)
			{
				info.children.add(AbstractCitizen.id);
				Main.ins.citizens.get(info.pair).citizen.info.children.add(AbstractCitizen.id);
			}
		}
		return citizens;
	}

	public void getPregnant(DNA fatherDna, CitizenProperties fatherProps)
	{
		if (curProps.physicalGender == Gender.Woman && fatherProps.physicalGender == Gender.Man)
			for (int i = 0; i < Math.pow(Main.random.nextInt((int) Math.pow(3, 3) + 1), 1d / 3d); i++)
			{
				int[] nucl = new int[DNA.size];
				for (int j = 0; j < nucl.length; j++)
					nucl[j] = Main.random.nextInt(DNA.size / Main.mutationChance) == 0 ? Main.random.nextInt(4)
							: (Main.random.nextBoolean() ? dna.nucl[i] : fatherDna.nucl[i]);
				info.fetuses.put(new DNA(nucl), Main.time);
			}
	}

	@Override
	public String toString()
	{
		return "Citizen [id=" + id + ", bioMather=" + bioMather + ", bioFather=" + bioFather + ", dna=" + dna
				+ ", born=" + born + ", bornProps=" + bornProps + ", x=" + x + ", y=" + y + ", z=" + z + ", info="
				+ info + ", curProps=" + curProps + ", death=" + death + ", fertileAge=" + fertileAge
				+ ", defertileAge=" + defertileAge + ", canBeFertile=" + canBeFertile + ", fertile=" + fertile
				+ ", rand=" + rand + ", deathCause=" + deathCause + ", timeOfOld=" + timeOfOld + ", nextOldDeathCheck="
				+ nextOldDeathCheck + ", ind=" + ind + ", toReset=" + toReset + "]";
	}

	public boolean canConceive()
	{
		boolean hasSmallChildren = false;
		for (Long id : info.children)
			if (Main.time - Main.ins.citizens.get(id).born < Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay
					* Main.daysInMonth * Main.monthsInSeason * Main.seasonsInYear * 3)
			{
				hasSmallChildren = true;
				break;
			}
		return info.fetuses.size() == 0 && !hasSmallChildren;
	}

	double fertileAge = Main.random.nextInt(7) + 9;
	double defertileAge = Main.random.nextInt(25) + 36;
	double adultAge = fertileAge + Math.max(2, 15 - fertileAge) + Main.random.nextInt(20 - (int) fertileAge - 2);
	boolean canBeFertile = Main.random.nextInt(500) > 0;
	boolean fertile = canBeFertile;

	public boolean isFertile()
	{
		return Main.time - born > Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay * Main.daysInMonth
				* Main.monthsInSeason * Main.seasonsInYear * fertileAge
				&& Main.time - born < Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay * Main.daysInMonth
						* Main.monthsInSeason * Main.seasonsInYear * defertileAge
				&& canBeFertile && fertile;
	}

	Point rand;
	String deathCause;
	int timeOfOld = Main.random.nextInt(60) + 30;
	double nextOldDeathCheck = 0;
	Point pathStart;

	public void simulateTimeUnit(long msInUnit)
	{
		if (!isDead())
			if (nextOldDeathCheck <= Main.time)
			{
				if (Main.random.nextInt((int) (timeOfOld * Main.seasonsInYear * Main.monthsInSeason * Main.daysInMonth
						+ (Main.time - born) / Main.secondsInMinute / Main.minutesInHour / Main.hoursInDay / 10)
						* 10000) < (Main.time - born) / Main.secondsInMinute / Main.minutesInHour / Main.hoursInDay)
				{
					death = Main.time;
					deathCause = "old";
				}
				nextOldDeathCheck = Main.time
						- Main.time % (Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay)
						+ Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay
						+ Main.random.nextInt((int) (Main.secondsInMinute * Main.minutesInHour * Main.hoursInDay));
			}
		if (!isDead())
		{
			for (int i = (int) msInUnit + 1; i > 0;)
			{
				int decr;
				if (curPath != null && pathStart != null ? !Main.pathsMatch(curPath, Main.ins.getPath(pathStart, rand))
						: true)
				{
					Point curPoint = new Point((int) x, (int) z);
					curPath = Main.ins.getPath(curPoint, rand);
					pathStart = curPoint;
					ind = 0;
				}
				else curPath = Main.ins.getPath(pathStart, rand);
				if (toReset == -1)
				{
					if (ind + 1 < curPath.length)
					{
						double xo = Math.min(Math.abs(curPath[ind + 1].x - x), i * getSpeed() / 1000d)
								* (curPath[ind + 1].x - x < 0 ? -1 : 1);
						double zo = Math.min(Math.abs(curPath[ind + 1].y - z), i * getSpeed() / 1000d)
								* (curPath[ind + 1].y - z < 0 ? -1 : 1);

						if (canWalk())
						{
							x += xo;
							z += zo;

							if (Math.round(x * 1000) / 1000d == curPath[ind + 1].x)
								if (Math.round(z * 1000) / 1000d == curPath[ind + 1].y)
								{
									x = Math.round(x * 1000) / 1000d;
									z = Math.round(z * 1000) / 1000d;
									ind++;
								}
						}
						decr = (int) Math.abs((xo + zo) * 1000d / getSpeed());
					}
					else
					{
						if (rand.x == x && rand.y == z)
							toReset = Main.random.nextInt(1000 * 60);
						decr = 1;
					}
				}
				else if (toReset == 0)
				{
					if (Main.ins.goToRandomPoint)
					{
						toReset = -1;
						ind = 0;
						rand = new Point(Main.random.nextInt(Main.ins.maxX - Main.ins.minX) + Main.ins.minX,
								Main.random.nextInt(Main.ins.maxZ - Main.ins.minZ) + Main.ins.minZ);
						decr = 1;
					}
					else decr = i;
				}
				else toReset -= (decr = Math.min(i, toReset));

				i -= decr;
			}
		}
	}

	double _speed = (1 + Main.random.nextInt(40) / 10d) / 3.6;

	public double getSpeed()
	{
		return _speed * (getAge() >= walkAge && getAge() <= adultAge
				? Math.max(1, (getAge() - walkAge) / (adultAge - walkAge))
				: 1);
	}

	private void ctzDebug(Object obj)
	{
		if (Main.ins.selectedCitizen == id)
			Main.ins.debug("CTZ" + id + ": " + obj);
	}

	int ind;
	int toReset = 0;
}