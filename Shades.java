import java.util.*;
import java.io.*;

enum NatureOfEchoChamber
{
	ReflectShade,
	MoreExtreme,
	LessExtreme,
}

enum ShadeDistribution
{
	Normal,
	Uniform,
	Mixture
}

enum NatureOfEncounter
{
	RallyTheBase,
	BuildBridges
}

class Individual {
	double currentShade;
	double coreShade;
	double acquiredShade;
	double alpha;
	double beta;
	double probEchoChamberEncounter;
	ArrayList<Double> shadeHistory;
	int id;	
	Individual(int id, double shade, double Alpha, double Beta, double probEchoChamberEncounter)
	{
		this.id = id;
		currentShade = shade;
		coreShade = shade;
		acquiredShade = shade;
		alpha = Alpha;
		beta = Beta;
		this.probEchoChamberEncounter = probEchoChamberEncounter;
		shadeHistory = new ArrayList<Double>();
		shadeHistory.add(shade);
	}

	double currentShade() { return currentShade;}
	double initialShade() { return shadeHistory.get(0);}
	String shadesInThePast() 
	{
		String result = "";
		for(int i = 0; i < shadeHistory.size(); i++)
			result = "" + shadeHistory.get(i) + "\t" + result;
		return result;
	}

	Boolean isEchoChamberEncounter(double randDouble)
	{
		if( randDouble <= probEchoChamberEncounter )
			return true;
		else
			return false; 
	}

	void increaseProbEchoChamberEncounter(double factor)
	{
		probEchoChamberEncounter *= factor;
		if( probEchoChamberEncounter > 1.0 )
			probEchoChamberEncounter = 1.0;
	}

	Boolean inBase(double encounterShade)
	{
		//if( (encounterShade < 5 && currentShade < 5) || (encounterShade >= 5 && currentShade >= 5) )
		if( Math.abs(currentShade - encounterShade) < 2.0 )
			return true;
		else
			return false;
	}

	void changeShade(double encounterShade)
	{
		//acquiredShade = beta*acquiredShade + (1.0 - beta)*encounterShade;

		if( acquiredShade < encounterShade )
			acquiredShade += beta;
		else
		{
			if( acquiredShade > encounterShade )
				acquiredShade -= beta;
		}

		if( acquiredShade > 10.0 ) acquiredShade = 10.0;
		if( acquiredShade < 0.0 ) acquiredShade = 0.0;
	}

	void changeShade(double encounterShade, Boolean buildBridges, Boolean saveShade)
	{
		//currentShade = (1.0 - beta)*currentShade + beta*shadeOfNewsSource;
		//currentShade = 0.5*coreShade + (0.5 - beta)*(currentShade - 0.5*coreShade) + beta*shadeOfNewsSource;
		
		if( buildBridges )
			changeShade(encounterShade);
		else
		{
			// this is a "rally the base" message

			// am I in the base

			if( inBase(encounterShade) )
				changeShade(encounterShade);
		}

		currentShade = alpha*coreShade + (1.0 - alpha)*acquiredShade;

		if( currentShade > 10.0 ) currentShade = 10.0;
		if( currentShade < 0.0 ) currentShade = 0.0;

		if( saveShade ) shadeHistory.add(currentShade);
	}
}

public class Shades {
	static Map<Integer,Individual> individuals;
	static int numIndividuals;
	static Random rand;
	static double probEchoChamberEncounter;
	static Boolean echoChamberAddictive;
	static NatureOfEchoChamber natureOfEchoChamber;
	static ShadeDistribution shadeDistribution;
	static double alpha;
	static double beta;
	static double probBuildBridgesMessage;
	static double addictionFactor;

	static void runSimulation(int numIterations)
	{
		double numECEncounters = 0;
		double numRegEncounters = 0;

		for(int i = 1; i <= numIterations; i++)
		{
			Boolean save = false;
			if( i % 10000 == 0 )
				save = true;
 
			for(int j = 0; j < numIndividuals; j++)
			{
				Individual ind = individuals.get(j);
				if( ind.isEchoChamberEncounter(rand.nextDouble()) )
				{
					numECEncounters += 1;
					echoChamberEncounter(j, save);
					if( echoChamberAddictive )
					{
						ind.increaseProbEchoChamberEncounter(addictionFactor);
					}
				}
				else	
				{
					numRegEncounters += 1;
					regularEncounter(j, save);
				}
			}
		}
		System.out.println("Num EC Encounters: " + numECEncounters);
		System.out.println("Num Reg Encounters: " + numRegEncounters);
	}

	public static void encounterWithLessExtremeEchoChamber(int indiv, Boolean save)
	{
		Individual ind = individuals.get(indiv);
		double shade = ind.currentShade();

		double r = rand.nextGaussian();

		shade = (shade + 5.0)/2.0;
		r *= 2.5/6.0;
		r += shade;
		if( r > 10.0 ) r = 10.0;
		if( r < 0 ) r = 0;

		/*
		if( rand.nextDouble() < probBuildBridgesMessage )
			ind.changeShade(r, true, save);		
		else
			ind.changeShade(r, false, save);		
		*/
		ind.changeShade(r, false, save);		
		return;
	}

	public static void encounterWithMoreExtremeEchoChamber(int indiv, Boolean save)
	{
		Individual ind = individuals.get(indiv);
		double shade = ind.currentShade();

		double r = rand.nextGaussian();

		Boolean tiltRight = true;
		if( shade < 5.0 )
			tiltRight = false;

		if(tiltRight)
		{
			// shade of 10 is extreme right
			shade = (shade + 10.0)/2.0;
			r *= 2.5/6.0;
			r += shade;
			if( r > 10.0 ) r = 10.0;
			if( r < 0 ) r = 0;
			//System.out.println(r);
			/*
			if( rand.nextDouble() < probBuildBridgesMessage )
				ind.changeShade(r, true, save);		
			else
				ind.changeShade(r, false, save);		
			*/
			ind.changeShade(r, false, save);		
			return;
		}
		// tiltRight is false
		shade = shade/2.0;
		r *= 2.5/6.0;
		r += shade;
		if( r > 10.0 ) r = 10.0;
		if( r < 0 ) r = 0;
		//System.out.println(r);
		/*
		if( rand.nextDouble() < probBuildBridgesMessage )
			ind.changeShade(r, true, save);		
		else
			ind.changeShade(r, false, save);		
		*/
		ind.changeShade(r, false, save);		
		return;
	}

	public static void echoChamberEncounter(int indiv, Boolean save)
	{
		Individual ind = individuals.get(indiv);
		double shade = ind.currentShade();

		double r = rand.nextGaussian();

		if(natureOfEchoChamber == NatureOfEchoChamber.ReflectShade)
		{
			r *= 2.5/6.0;
				// An echo chamber has much smaller std dev in its shades
			r += shade;
			if( r > 10.0 ) r = 10.0;
			if( r < 0 ) r = 0;

			/*
			if( rand.nextDouble() < probBuildBridgesMessage )
				ind.changeShade(r, true, save);		
			else
				ind.changeShade(r, false, save);		
			*/
			ind.changeShade(r, false, save);		
				// an echo chamber never means to build bridges
			return;
		}

		if(natureOfEchoChamber == NatureOfEchoChamber.MoreExtreme)
		{
			encounterWithMoreExtremeEchoChamber(indiv, save);
			return;
		}

		if(natureOfEchoChamber == NatureOfEchoChamber.LessExtreme)
		{
			encounterWithLessExtremeEchoChamber(indiv, save);
			return;
		}
	}

	public static void regularEncounter(int indiv, Boolean save)
	{
		Individual ind = individuals.get(indiv);

		double r = 0;

		if( shadeDistribution == ShadeDistribution.Normal )
		{
				r = rand.nextGaussian();
				r *= 10.0/6.0;
				r += 5.0;
		}

		if( shadeDistribution == ShadeDistribution.Uniform )
		{
				r = rand.nextDouble()*10.0;
		}

		if( shadeDistribution == ShadeDistribution.Mixture )
		{
				double shade = 7.5;
				if( rand.nextDouble() < 0.5 )
					shade = 2.5;
				r = rand.nextGaussian();
				r *= 5.0/6.0;
				r += shade;
		}
		if( r > 10.0 ) r = 10.0;
		if( r < 0 ) r = 0;
		if( rand.nextDouble() < probBuildBridgesMessage )
			ind.changeShade(r, true, save);		
		else
			ind.changeShade(r, false, save);		
	}

	public static void main(String[] args) throws FileNotFoundException {
		individuals = new HashMap<Integer,Individual>();


		switch( Integer.parseInt(args[0]) )
		{
			case 1: natureOfEchoChamber = NatureOfEchoChamber.ReflectShade;
				break;
			case 2: natureOfEchoChamber = NatureOfEchoChamber.MoreExtreme;
				break;
			case 3: natureOfEchoChamber = NatureOfEchoChamber.LessExtreme;
				break;
		}

		switch( Integer.parseInt(args[1]) )
		{
			case 1: shadeDistribution = ShadeDistribution.Normal;
				break;
			case 2: shadeDistribution = ShadeDistribution.Uniform;
				break;
			case 3: shadeDistribution = ShadeDistribution.Mixture;
				break;
		}

		probEchoChamberEncounter = Double.parseDouble(args[2]);

		switch( Integer.parseInt(args[3]) )
		{
			case 0: echoChamberAddictive = false;
				break;
			case 1: echoChamberAddictive = true;
				break;
		}

		alpha = Double.parseDouble(args[4]);
		beta = Double.parseDouble(args[5]);
		probBuildBridgesMessage = Double.parseDouble(args[6]);

		File fileIndividuals = new File(args[7]);
		Scanner inIndividuals = new Scanner(fileIndividuals);

		int id = 0;

		while(inIndividuals.hasNextLine())
		{
			double shade = Double.parseDouble(inIndividuals.nextLine());
			individuals.put(id, new Individual(id, shade, alpha, beta, probEchoChamberEncounter) );
			id++;
		}
		inIndividuals.close();
		numIndividuals = id;

		int numIterations = Integer.parseInt(args[8]);
		long seed = Integer.parseInt(args[9]);
		rand = new Random(seed);

		File outIndividualShadesFile = new File(args[10]);

		addictionFactor = Double.parseDouble(args[11]);

		runSimulation(numIterations);

		PrintWriter outIndividualShades = new PrintWriter(outIndividualShadesFile);
		for(int i = 0; i < numIndividuals; i++)
		{
			//double currentShade = individuals.get(i).currentShade(); 
			//double initialShade = individuals.get(i).initialShade(); 
			//double diff = currentShade - initialShade;
			//outIndividualShades.println(currentShade + " " + initialShade + " " + diff); 
			//individuals.get(i).print();
			String s = individuals.get(i).shadesInThePast();
			outIndividualShades.println(s);
		} 
		outIndividualShades.close();
	}
}
