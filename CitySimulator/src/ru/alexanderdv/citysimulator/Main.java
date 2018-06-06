package ru.alexanderdv.citysimulator;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.swing.Timer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application
{
	public static final Random random = new Random();;

	private Pane pane;
	private VBox toolsVBox;
	private Label timeLabel;
	private TextArea consoleArea;
	private ToggleButton mapButton;
	private Canvas mapCanvas;
	private GraphicsContext mapGraphics;
	private ToggleButton treeButton;
	private Canvas treeCanvas;
	private GraphicsContext treeGraphics;
	private ToggleButton phoneButton;
	private Canvas phoneCanvas;
	private GraphicsContext phoneGraphics;
	private CheckBox showPath;
	private CheckBox showAllPaths;

	private Stage stage;

	private String fixed = "", editable = "";

	static final long seasonsInYear = 4;

	public static final int mutationChance = 3;
	static long speed = 3, secondsInMinute = 60, minutesInHour = 60, hoursInDay = 24, daysInMonth = 8,
			monthsInSeason = 3;
	static double startTime = secondsInMinute * minutesInHour * hoursInDay * daysInMonth * monthsInSeason
			* seasonsInYear * 1920 + 60 * 60 * 7 + 3400;
	static double time = startTime;

	public static void main(String[] args)
	{
		launch(args);
	}

	static Main ins;
	boolean goToRandomPoint = false;

	@Override
	public void start(Stage mainStage) throws Exception
	{
		this.stage = mainStage;
		ins = this;
		stage.setScene(new Scene(pane = new Pane(
				toolsVBox = new VBox(5, timeLabel = new Label(), consoleArea = new TextArea(),
						showPath = new CheckBox("Show path"), showAllPaths = new CheckBox("Show all paths")),
				mapButton = new ToggleButton("Map"), mapCanvas = new Canvas(), treeButton = new ToggleButton("Tree"),
				treeCanvas = new Canvas(), phoneButton = new ToggleButton("Phone"), phoneCanvas = new Canvas())));
		mapGraphics = mapCanvas.getGraphicsContext2D();
		treeGraphics = treeCanvas.getGraphicsContext2D();
		phoneGraphics = phoneCanvas.getGraphicsContext2D();
		stage.setMinWidth(400);
		stage.setMinHeight(200);
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.getScene().widthProperty().addListener((l, o, n) ->
		{
			pane.setPrefWidth(n.intValue());
			toolsVBox.setLayoutX(0);
			toolsVBox.setPrefWidth(200);
			// tabPane.setLayoutX(toolsVBox.getPrefWidth());
			// tabPane.setPrefWidth(n.intValue() - tabPane.getLayoutX());
			mapButton.setLayoutX(toolsVBox.getPrefWidth());
			mapButton.setPrefWidth(100);
			treeButton.setLayoutX(mapButton.getLayoutX() + mapButton.getPrefWidth());
			treeButton.setPrefWidth(100);
			phoneButton.setLayoutX(treeButton.getLayoutX() + treeButton.getPrefWidth());
			phoneButton.setPrefWidth(100);

			mapCanvas.setLayoutX(toolsVBox.getPrefWidth());
			mapCanvas.setWidth(n.intValue() - mapCanvas.getLayoutX());

			treeCanvas.setLayoutX(toolsVBox.getPrefWidth());
			treeCanvas.setWidth(n.intValue() - treeCanvas.getLayoutX());

			phoneCanvas.setLayoutX(toolsVBox.getPrefWidth());
			phoneCanvas.setWidth(n.intValue() - phoneCanvas.getLayoutX());
		});
		stage.getScene().heightProperty().addListener((l, o, n) ->
		{
			pane.setPrefHeight(n.intValue());
			toolsVBox.setLayoutY(0);
			toolsVBox.setPrefHeight(n.intValue());

			mapCanvas.setLayoutY(25);
			mapCanvas.setHeight(n.intValue() - mapCanvas.getLayoutY());

			treeCanvas.setLayoutY(25);
			treeCanvas.setHeight(n.intValue() - treeCanvas.getLayoutY());

			phoneCanvas.setLayoutY(25);
			phoneCanvas.setHeight(n.intValue() - phoneCanvas.getLayoutY());
		});
		consoleArea.setEditable(false);
		consoleArea.setWrapText(true);
		consoleArea.selectionProperty().addListener((l, o, n) ->
		{
			if (n.getLength() != 0)
				consoleArea.deselect();
		});
		consoleArea.setOnKeyPressed(e ->
		{
			if (e.getCode() == KeyCode.ENTER)
			{
				String[] args = editable.split(" ");
				fixed += editable + "\r\n";
				editable = "";
				switch (args[0])
				{
					case "setspeed":
						speed = Integer.parseInt(args[1]);
						break;
					case "getmchc":
						int max = 0;
						long id = 0;
						for (AbstractCitizen citizen : citizens.values().toArray(new AbstractCitizen[0]))
							if (citizen.citizen.info.children.size() > max)
							{
								max = citizen.citizen.info.children.size();
								id = citizen.citizen.id;
							}
						printConsole("Citizen " + id + " has " + max + " children");
						break;
					case "getspeed":
						printConsole(speed);
						break;
					case "settime":
						time = Integer.parseInt(args[1]);
						break;
					case "gettime":
						printConsole(time);
						break;
					case "getctzs":
						String ids = "";
						boolean compareB = args[3].equals("true");
						String compareS = args[3];
						// double compareN = args[3];
						for (AbstractCitizen absCtz : citizens.values())
						{
							String valueS = null;
							String valueN = null;
							boolean valueB = false;

							boolean isB = false;
							boolean isS = false;
							boolean isN = false;

							boolean match = false;
							switch (args[1])
							{
								case "canwalk":
									valueB = absCtz.citizen.canWalk();
									isB = true;
									break;
								case "surname":
									valueS = absCtz.citizen.info.surname;
									isS = true;
									break;
							}
							if (isB)
								match = valueB == compareB;
							if (isS)
								switch (args[2].replace("!", ""))
								{
									case "contains":
										match = valueS.contains(compareS);
										break;
									case "containsin":
										match = compareS.contains(valueS);
										break;
									case "is":
										match = valueS.equals(compareS);
										break;
								}
							if (match == !args[2].contains("!"))
								ids += absCtz.id + ", ";
						}
						printConsole(
								"Citizens' with ids " + ids + " property " + args[1] + " " + args[2] + " " + args[3]);
						break;
					case "select":
						selectedCitizen = Long.parseLong(args[1]);
						break;
					case "addtime":
						time += Integer.parseInt(args[1]);
						break;
					case "removetime":
						time -= Integer.parseInt(args[1]);
						break;
					case "addcitizen":
						citizens.put(new Long(citizens.size()), new AbstractCitizen(new Citizen(citizens.size(),
								Integer.parseInt(args[1]), Integer.parseInt(args[2]), null, time)));
						break;
					case "getcitizen":
						System.out.println(citizens.get(Long.parseLong(args[1])).citizen);
						break;
					case "setpath":
						citizens.get(Long.parseLong(args[1])).citizen.toReset = -2;
						citizens.get(Long.parseLong(args[1])).citizen.rand = new Point(Integer.parseInt(args[2]),
								Integer.parseInt(args[3]));
						break;
				}
			}
			else if (e.getCode() == KeyCode.BACK_SPACE)
				editable = editable.substring(0, Math.max(0, editable.length() - 1));
			updateConsole();
		});
		consoleArea.setOnKeyTyped(e ->
		{
			if (e.getCharacter() != null)
				if (!e.getCharacter().equals("CHAR_UNDEFINED"))
					if (Character.isLetterOrDigit(e.getCharacter().charAt(0)) || e.getCharacter().equals(" ")
							|| e.getCharacter().equals(".") || e.getCharacter().equals("-"))
					{
						editable += e.getCharacter();
					}
			updateConsole();
		});
		mapCanvas.setOnMousePressed(e ->
		{
			lastX = e.getX();
			lastY = e.getY();
		});
		mapCanvas.setOnMouseDragged(e ->
		{
			double curX = e.getX(), curY = e.getY();
			Character c = null;
			switch (e.getButton())
			{
				case PRIMARY:
					x = Math.max(minX, Math.min(x + (lastX - curX) / metersInPixels * y, maxX));
					z = Math.max(minZ, Math.min(z + (lastY - curY) / metersInPixels * y, maxZ));
					break;
				case MIDDLE:
					if (c == null)
						c = ' ';
					break;
				case SECONDARY:
					if (c == null)
						c = '#';
					break;
				default:
					break;
			}
			if (c != null)
			{
				Point p = new Point((int) toMapX(curX), (int) toMapZ(curY));
				if (getNavCell(p.x, p.y) != c.charValue())
				{
					setNavCell(p.x, p.y, c.charValue());
					paths.clear();
				}
				// {
				// for (PathDir dir : paths.keySet().toArray(new PathDir[0]))
				// for (Point point : paths.get(dir))
				// if (point.x == p.x && point.y == p.y)
				// {
				// paths.remove(dir);
				// break;
				// }
				// }
			}
			lastX = curX;
			lastY = curY;
		});
		mapCanvas.setOnScroll(e -> y = Math.max(minY, Math.min(y - e.getDeltaY() / 100, maxY)));

		treeCanvas.setOnMousePressed(e ->
		{
			lastX = e.getX();
			lastY = e.getY();
		});
		treeCanvas.setOnMouseDragged(e ->
		{
			double curX = e.getX(), curY = e.getY();
			switch (e.getButton())
			{
				case PRIMARY:
					genealogicalTreeMoveX += (lastX - curX) / genealogicalTreeUnzoom;
					genealogicalTreeMoveY += (lastY - curY) / genealogicalTreeUnzoom;
					break;
				case MIDDLE:
					break;
				case SECONDARY:
					break;
				default:
					break;
			}
			lastX = curX;
			lastY = curY;
		});
		treeCanvas.setOnScroll(e -> genealogicalTreeUnzoom = Math.max(genealogicalTreeUnzoom - e.getDeltaY() / 100, 1));
		new Timer(1000 / 60, e -> updateGraphics()).start();
		new Timer(500, e -> updateConsole()).start();
		stage.show();
		new Thread(() -> initPairs()).start();
		new Thread(() -> new Timer(1,
				e -> simulateTimeUnit((-lt + (lt = Calendar.getInstance().getTimeInMillis())) * speed)).start())
						.start();
		mapButton.setOnAction(e ->
		{
			mapButton.setSelected(true);
			treeButton.setSelected(!mapButton.isSelected());
			phoneButton.setSelected(!mapButton.isSelected());
			updateTabs();
		});
		treeButton.setOnAction(e ->
		{
			treeButton.setSelected(true);
			mapButton.setSelected(!treeButton.isSelected());
			phoneButton.setSelected(!treeButton.isSelected());
			updateTabs();
		});
		phoneButton.setOnAction(e ->
		{
			phoneButton.setSelected(true);
			treeButton.setSelected(!phoneButton.isSelected());
			mapButton.setSelected(!phoneButton.isSelected());
			updateTabs();
		});
		mapButton.fire();
	}

	private void updateTabs()
	{
		mapCanvas.setVisible(mapButton.isSelected());
		treeCanvas.setVisible(treeButton.isSelected());
		phoneCanvas.setVisible(phoneButton.isSelected());
	}

	double genealogicalTreeUnzoom = 10;
	double genealogicalTreeMoveX = 0;
	double genealogicalTreeMoveY = 0;

	@SuppressWarnings("unchecked")
	private void updateGenealogicalTree()
	{
		Platform.runLater(() ->
		{
			if (citizens.get(selectedCitizen) == null)
			{
				selectedCitizen++;
				return;
			}
			genealogicalTreeCitizens.clear();
			treeGraphics.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

			treeGraphics.translate(treeCanvas.getWidth() / 2, treeCanvas.getHeight() / 2);

			Citizen citizen = citizens.get(selectedCitizen).citizen;

			if (citizens.get(citizen.bioFather) != null && citizens.get(citizen.bioMather) != null)
			{
				Citizen father = citizens.get(citizen.bioFather).citizen;
				Citizen mather = citizens.get(citizen.bioMather).citizen;

				double le1 = 0, yoffset1 = 0;
				ArrayList<Long> children1 = (ArrayList<Long>) father.info.children.clone();
				children1.remove(citizen.id);

				for (long l : children1)
				{
					le1 += citizens.get(l).citizen.getAge()
							* (l == children1.get(0)/* || i == father.info.children.size() - 1 */ ? 1 : 2);
					yoffset1 = Math.max(citizens.get(l).citizen.getAge(), yoffset1);
				}
				double le22 = 0;
				for (long l : children1)
				{
					le22 += children1.get(0) == l ? 0 : citizens.get(l).citizen.getAge();
					double x = (-le1 + le22 - citizen.getAge() * 2)
							* (citizen.curProps.physicalGender == Gender.Man ? 1 : -1);

					drawCitizenInTree(x, 0, citizens.get(l).citizen.getAge(), citizens.get(l).citizen);
					treeGraphics.strokeLine(x, -yoffset1, x, -citizens.get(l).citizen.getAge() / 2);
					le22 += citizens.get(l).citizen.getAge();

				}
				double xoffset2 = (-le1 - citizen.getAge() * 2)
						* (citizen.curProps.physicalGender == Gender.Man ? 1 : -1);

				treeGraphics.strokeLine(citizen.getAge() * (citizen.curProps.physicalGender == Gender.Man ? -1 : 1),
						-yoffset1, citizen.getAge() * (citizen.curProps.physicalGender == Gender.Man ? -1 : 1), 0);
				treeGraphics.strokeLine(xoffset2, -yoffset1,
						-citizen.getAge() * (citizen.curProps.physicalGender == Gender.Man ? 1 : -1), -yoffset1);

				double max2 = Math.max(father.getAge(), mather.getAge());
				double xoffset22 = (-le1 / 2 - citizen.getAge() * 2)
						* (citizen.curProps.physicalGender == Gender.Man ? 1 : -1);

				drawCitizenInTree(-father.getAge() + xoffset22 / 2, -yoffset1 - max2, father.getAge(), father);

				drawCitizenInTree(+mather.getAge() + xoffset22 / 2, -yoffset1 - max2, mather.getAge(), mather);

				treeGraphics.strokeLine(-father.getAge() / 2 + xoffset22 / 2, -max2 - yoffset1,
						+mather.getAge() / 2 + xoffset22 / 2, -max2 - yoffset1);

				treeGraphics.strokeLine(xoffset22 / 2, -yoffset1, xoffset22 / 2, -max2 - yoffset1);
			}
			if (citizens.get(citizen.info.pair) != null)
			{
				Citizen pair = citizens.get(citizen.info.pair).citizen;

				double max = Math.max(citizen.getAge(), pair.getAge());

				drawCitizenInTree(-citizen.getAge(), 0, citizen.getAge(), citizen);

				drawCitizenInTree(+pair.getAge(), 0, pair.getAge(), pair);

				treeGraphics.strokeLine(-citizen.getAge() / 2, 0, +pair.getAge() / 2, 0);

				treeGraphics.strokeLine(0, 0, 0, max);
				double le = 0, yoffset = 0;
				for (int i = 0; i < citizen.info.children.size(); i++)
				{
					le += citizens.get(citizen.info.children.get(i)).citizen.getAge()
							* (i == 0 || i == citizen.info.children.size() - 1
									? (i == 0 && i == citizen.info.children.size() - 1 ? 0 : 1)
									: 2);
					yoffset = Math.max(citizens.get(citizen.info.children.get(i)).citizen.getAge(), yoffset);
				}

				treeGraphics.strokeLine(-le / 2, max, +le / 2, max);
				double le2 = 0;
				for (long l : citizen.info.children)
				{
					le2 += citizen.info.children.get(0) == l ? 0 : citizens.get(l).citizen.getAge();
					double x = -le / 2 + le2;
					drawCitizenInTree(x, max + yoffset, citizens.get(l).citizen.getAge(), citizens.get(l).citizen);
					treeGraphics.strokeLine(x, max, x, max + yoffset - citizens.get(l).citizen.getAge() / 2);
					le2 += citizens.get(l).citizen.getAge();

				}
			}
			else drawCitizenInTree(-citizen.getAge(), 0, citizen.getAge(), citizen);

			treeGraphics.translate(-treeCanvas.getWidth() / 2, -treeCanvas.getHeight() / 2);

			treeCanvas.setOnMouseClicked(e ->
			{
				double x = e.getX() - treeCanvas.getWidth() / 2, y = e.getY() - treeCanvas.getHeight() / 2;
				for (SimpleEntry<Point, Point> area : genealogicalTreeCitizens.keySet())
					if (x >= area.getKey().x)
						if (x <= area.getValue().x)
							if (y >= area.getKey().y)
								if (y <= area.getValue().y)
									if (e.getButton() == MouseButton.SECONDARY)
									{
										Stage s = new Stage();
										s.initOwner(stage);
										s.setScene(new Scene(
												new TextArea(genealogicalTreeCitizens.get(area).toDisplayLook())));
										s.sizeToScene();
										s.showAndWait();
										break;
									}
									else if (e.getButton() == MouseButton.PRIMARY)
									{
										selectedCitizen = genealogicalTreeCitizens.get(area).id;
										break;
									}

			});
		});
	}

	HashMap<SimpleEntry<Point, Point>, Citizen> genealogicalTreeCitizens = new HashMap<SimpleEntry<Point, Point>, Citizen>();

	private void drawCitizenInTree(double x, double y, double d, Citizen citizen)
	{
		treeGraphics.setFill(citizen.curProps.physicalGender == Gender.Man ? Color.CORNFLOWERBLUE : Color.PINK);
		genealogicalTreeCitizens.put(new SimpleEntry<Point, Point>(new Point((int) (x - d / 2), (int) (y - d / 2)),
				new Point((int) (x + d / 2), (int) (y + d / 2))), citizen);
		treeGraphics.fillOval(x - d / 2, y - d / 2, d, d);
		treeGraphics.setFill(Color.BLACK);
		treeGraphics.fillText(citizen.info.surname + "\n" + citizen.info.name, x - d / 2, y - d / 2 + 10);
	}

	long selectedCitizen;

	private void updateGraphics()
	{
		Platform.runLater(() ->
		{
			timeLabel.setText(toDisplayDate(time));
		});
		if (mapButton.isSelected())
			updateMap();
		else updateGenealogicalTree();
	}

	@SuppressWarnings("unchecked")
	private void updateMap()
	{
		Platform.runLater(() ->
		{
			HashMap<Long, AbstractCitizen> citizens = (HashMap<Long, AbstractCitizen>) this.citizens.clone();
			mapGraphics.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
			for (int x = minX; x < maxX - minX; x++)
				for (int z = minZ; z < maxZ - minZ; z++)
				{
					if (getNavCell(x, z) == '#')
					{
						mapGraphics.setFill(Color.BLUE);
						fillRect(x, z, 1, 1);
					}
				}

			mapGraphics.setFill(Color.SEAGREEN);
			for (AbstractCitizen AbstractCitizen : citizens.values())
				if (showAllPaths.isSelected()
						|| (showPath.isSelected() && AbstractCitizen.citizen.id == selectedCitizen))
					if (AbstractCitizen.citizen.curPath != null)
						for (Point point : AbstractCitizen.citizen.curPath)
							fillRect(point.x, point.y, 1, 1);
						mapGraphics.setFill(Color.RED);
			for (AbstractCitizen AbstractCitizen : citizens.values())
				if (showAllPaths.isSelected()
						|| (showPath.isSelected() && AbstractCitizen.citizen.id == selectedCitizen))
					if (AbstractCitizen.citizen.rand != null)
						fillRect(AbstractCitizen.citizen.rand.x, AbstractCitizen.citizen.rand.y, 1, 1);

			mapGraphics.setFill(Color.BLACK);
			for (AbstractCitizen AbstractCitizen : citizens.values())
				fillOval(AbstractCitizen.citizen.x, AbstractCitizen.citizen.z,
						Math.sqrt(AbstractCitizen.citizen.getAge()) / 10d,
						Math.sqrt(AbstractCitizen.citizen.getAge()) / 10d);

			mapGraphics.setFill(Color.BLACK);
			int xo = 30, yo = 10;
			mapGraphics.fillRect(xo, yo, mapCanvas.getWidth() - xo, toCanvasZ(-z) - yo);
			mapGraphics.fillRect(xo, yo, toCanvasX(-x) - xo, mapCanvas.getHeight() - yo);
			mapGraphics.fillRect(xo, toCanvasZ(maxZ - minZ - z), mapCanvas.getWidth() - xo,
					mapCanvas.getHeight() - toCanvasZ(-z));
			mapGraphics.fillRect(toCanvasX(maxX - minX - x), yo, mapCanvas.getWidth() - toCanvasX(-x),
					mapCanvas.getHeight() - yo);

			for (double a = 0, opacity; a < 3
					&& (opacity = Math.pow(minY, 0.8) / Math.pow(y, 0.8) * Math.pow(4, a)) != 33.11
					&& (opacity = opacity > 1 && opacity < 2 ? Math.abs(1 - Math.abs(1 - opacity))
							: opacity) != 33.11; a++)
				if (opacity > 0.2 && opacity <= 1)
				{
					mapGraphics.setFill(new Color(0, 0, 0.7, opacity));
					int vlines = (int) (mapCanvas.getWidth() / metersInPixels * y / Math.pow(10, a));
					for (int i = 0; i < vlines; i++)
					{
						double xxx = toCanvasX((i - vlines / 2) * Math.pow(10, a) - x % Math.pow(10, a)),
								www = metersInPixels / y / 100 * Math.pow(10, a);
						mapGraphics.fillRect(xxx, 0, www, mapCanvas.getHeight());
						if (opacity > 0.4)
							mapGraphics.fillText(Math.round(toMapX(xxx)) + "", xxx + www, yo,
									metersInPixels / y * Math.pow(10, a));
					}
					int hlines = (int) (mapCanvas.getHeight() / metersInPixels * y / Math.pow(10, a));
					for (int i = 0; i < hlines; i++)
					{
						double zzz = toCanvasZ((i - hlines / 2) * Math.pow(10, a) - z % Math.pow(10, a)),
								hhh = metersInPixels / y / 100 * Math.pow(10, a);
						mapGraphics.fillRect(0, zzz, mapCanvas.getWidth(), hhh);
						if (opacity > 0.4)
							mapGraphics.fillText(Math.round(toMapZ(zzz)) + "", 0, zzz + hhh + yo, xo);
					}
				}
		});
	}

	private synchronized void initPairs()
	{
		for (int i = 0; i < 20; i++)
		{
			int[] nucl = DNA.random().nucl;
			nucl[0] = (i % 2) * 2 + random.nextInt(2);
			citizens.put(new Long(citizens.size()),
					new AbstractCitizen(new Citizen(citizens.size(), -1, -1, new DNA(nucl), time)));
		}
		for (int i = 0; i < 80 * daysInMonth * monthsInSeason * seasonsInYear; i++)
		{
			time += secondsInMinute * minutesInHour * hoursInDay;
			for (AbstractCitizen AbstractCitizen : citizens.values())
				AbstractCitizen.citizen.simulateTimeUnit(1000 * secondsInMinute * minutesInHour * hoursInDay);
			for (AbstractCitizen absCtz : citizens.values())
				if (!absCtz.citizen.isDead() && time - absCtz.citizen.born >= secondsInMinute * minutesInHour
						* hoursInDay * daysInMonth * monthsInSeason * seasonsInYear * 18
						&& absCtz.citizen.info.pair == -1)
					for (AbstractCitizen absCtz2 : citizens.values())
						if (!absCtz2.citizen.isDead()
								&& time - absCtz2.citizen.born >= secondsInMinute * minutesInHour * hoursInDay
										* daysInMonth * monthsInSeason * seasonsInYear * 18
								&& absCtz2.citizen.info.pair == -1)
						{
							Citizen ct1 = absCtz.citizen;
							CitizenProperties props1 = ct1.curProps;
							Citizen ct2 = absCtz2.citizen;
							CitizenProperties props2 = ct2.curProps;
							if (props1.physicalGender != props2.physicalGender)
								if (random
										.nextInt((int) (Math.abs(ct1.born - ct2.born) / secondsInMinute / minutesInHour
												/ hoursInDay / daysInMonth / monthsInSeason / seasonsInYear * 50)
												+ 1) == 0)
								{
									HistocompatibilityProtein[] hcps1 = props1.histocompatibilityProteins;
									HistocompatibilityProtein[] hcps2 = props2.histocompatibilityProteins;
									if (random.nextInt(hcps1[0] != hcps2[0] ? 2 : 4) == 0)
										if (random.nextInt(hcps1[1] != hcps2[1] ? 2 : 4) == 0)
											if (random.nextInt(hcps1[2] != hcps2[2] ? 2 : 4) == 0)
												if (random.nextInt(hcps1[3] != hcps2[3] ? 2 : 4) == 0)
													if (random.nextInt(hcps1[4] != hcps2[4] ? 2 : 4) == 0)
														if (random.nextInt(hcps1[5] != hcps2[5] ? 2 : 4) == 0)
														{
															ct1.info.pair = ct2.id;
															ct2.info.pair = ct1.id;
															break;
														}
								}
						}
			for (AbstractCitizen AbstractCitizen : citizens.values())
				if (!AbstractCitizen.citizen.isDead())
					if (AbstractCitizen.citizen.curProps.physicalGender == Gender.Man)
						if (AbstractCitizen.citizen.info.pair != -1)
						{
							Citizen man = AbstractCitizen.citizen;
							Citizen woman = citizens.get(AbstractCitizen.citizen.info.pair).citizen;
							if (man.isFertile())
								if (woman.isFertile())
									if (woman.canConceive())
										if (random.nextInt(
												200 + man.info.children.size() * 60 * man.info.children.size()) == 0)
											woman.getPregnant(man.dna, man.curProps);
						}
			ArrayList<AbstractCitizen> ctzs = new ArrayList<AbstractCitizen>();
			for (AbstractCitizen absCtz : citizens.values())
				if (!absCtz.citizen.isDead())
					if (absCtz.citizen.curProps.physicalGender == Gender.Woman)
						ctzs.addAll(absCtz.citizen.tryToBorn());
			for (AbstractCitizen abstractCitizen : ctzs)
				citizens.put(new Long(citizens.size()), abstractCitizen);
		}
		goToRandomPoint = true;
	}

	long i, g;
	long lt = Calendar.getInstance().getTimeInMillis();
	boolean timeGo = true;

	private synchronized void simulateTimeUnit(long msInUnit)
	{
		if (msInUnit < 1)
			return;
		time += msInUnit / 1000d;

		for (AbstractCitizen citizen : citizens.values().toArray(new AbstractCitizen[0]))
			citizen.citizen.simulateTimeUnit(msInUnit);
	}

	int ind = 0;

	int minX = 0, minY = 3, minZ = 0;
	int maxX = 200, maxY = 100, maxZ = 200;

	// Point[][][][][] paths = new Point[maxX - minX][maxZ - minZ][maxX - minX][maxZ
	// - minZ][];
	double x, y, z;
	final double metersInPixels = 90;
	char[][] navigationMap = new char[maxX - minX][maxZ - minZ];
	{
		for (int i = 0; i < navigationMap.length; i++)
			Arrays.fill(navigationMap[i], ' ');
	}

	private Point[] findPath(Point start, Point end)
	{
		int[][] waveStrength = new int[maxX - minX][maxZ - minZ];
		for (int i = 0; i < waveStrength.length; i++)
			Arrays.fill(waveStrength[i], Integer.MAX_VALUE);
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(end);
		waveStrength[end.x][end.y] = 0;
		for (; !points.isEmpty();)
		{
			Point p = points.remove(0);
			int pathLength = waveStrength[p.x - minX][p.y - minZ] + (getNavCell(p.x, p.y) == '#' ? 0 : 1);
			Point[] neighbors = new Point[] { new Point(p.x + 1, p.y), new Point(p.x, p.y + 1), new Point(p.x - 1, p.y),
					new Point(p.x, p.y - 1) };
			for (Point neighbor : neighbors)
				if (neighbor.x < maxX && neighbor.y < maxZ && neighbor.x >= minX && neighbor.y >= minZ)
					if (getNavCell(neighbor.x, neighbor.y) != '#')
						if (pathLength < waveStrength[neighbor.x - minX][neighbor.y - minZ])
						{
							waveStrength[neighbor.x - minX][neighbor.y - minZ] = pathLength;
							points.add(new Point(neighbor.x - minX, neighbor.y - minZ));
						}
		}
		ArrayList<Point> path = new ArrayList<Point>();
		Point p = start;
		for (; p != null;)
		{
			path.add(p);
			Point[] neighbors = new Point[] { new Point(p.x + 1, p.y), new Point(p.x, p.y + 1), new Point(p.x - 1, p.y),
					new Point(p.x, p.y - 1) };
			int min = waveStrength[p.x - minX][p.y - minZ];
			for (Point neighbor : neighbors)
				if (neighbor.x < maxX && neighbor.y < maxZ && neighbor.x >= minX && neighbor.y >= minZ)
					if (waveStrength[neighbor.x - minX][neighbor.y - minZ] < min)
					{
						min = waveStrength[neighbor.x - minX][neighbor.y - minZ];
						p = neighbor;
					}
			if (path.contains(p))
				p = null;
		}
		return path.toArray(new Point[0]);
	}

	HashMap<Long, AbstractCitizen> citizens = new HashMap<Long, AbstractCitizen>();

	double lastX, lastY;

	private PointD round(PointD point)
	{
		return PointD.get(point.x - point.x % 1, point.z - point.z % 1);
	}

	private void fillRect(double x, double z, double w, double h)
	{
		mapGraphics.fillRect(((x - this.x) / y * metersInPixels + mapCanvas.getWidth() / 2),
				((z - this.z) / y * metersInPixels + mapCanvas.getHeight() / 2), w / y * metersInPixels,
				h / y * metersInPixels);
	}

	private void fillOval(double x, double z, double w, double h)
	{
		mapGraphics.fillOval(((x - this.x) / y * metersInPixels + mapCanvas.getWidth() / 2),
				((z - this.z) / y * metersInPixels + mapCanvas.getHeight() / 2), w / y * metersInPixels,
				h / y * metersInPixels);
	}

	HashMap<PathDir, Point[]> paths = new HashMap<PathDir, Point[]>();

	public synchronized Point[] getPath(Point start, Point end)
	{
		if (start == null || end == null)
			return null;
		Point[] path = null;
		PathDir pathDir = new PathDir(start.x, start.y, end.x, end.y);
		do
		{
			try
			{
				if (paths.get(pathDir) == null)
					paths.put(pathDir, findPath(start, end));
				path = paths.get(pathDir);
			}
			catch (NullPointerException e)
			{
				System.err.println(e.getMessage() + " in getPath");
			}
		}
		while (path == null);
		return path;
	}

	private void setNavCell(int x, int z, char c)
	{
		if (x >= minX && x < maxX && z >= minZ && z < maxZ)
			navigationMap[x - minX][z - minZ] = c;
	}

	private char getNavCell(int x, int z)
	{
		if (x >= minX && x < maxX && z >= minZ && z < maxZ)
			return navigationMap[x - minX][z - minZ];
		else return (char) -1;
	}

	private double toMapX(double x)
	{
		return this.x + (x - mapCanvas.getWidth() / 2) / metersInPixels * y;
	}

	private double toMapZ(double z)
	{
		return this.z + (z - mapCanvas.getHeight() / 2) / metersInPixels * y;
	}

	private double toCanvasX(double x)
	{
		return (x) * metersInPixels / y + mapCanvas.getWidth() / 2;
	}

	private double toCanvasZ(double z)
	{
		return (z) * metersInPixels / y + mapCanvas.getHeight() / 2;
	}

	private void printConsole(Object obj)
	{
		fixed += obj.toString() + "\r\n";
		updateConsole();
	}

	private void updateConsole()
	{
		Platform.runLater(() -> consoleArea.setText(fixed + editable
				+ (Calendar.getInstance().getTimeInMillis() / 500 % 2 == 0 && consoleArea.isFocused() ? "|" : "")));
	}

	public static String toDisplayDate(double timeInSeconds)
	{
		String s = (long) timeInSeconds / (secondsInMinute * minutesInHour * hoursInDay) % daysInMonth + ".";
		s += (long) timeInSeconds / (secondsInMinute * minutesInHour * hoursInDay * daysInMonth)
				% (monthsInSeason * seasonsInYear) + ".";
		s += (long) timeInSeconds
				/ (secondsInMinute * minutesInHour * hoursInDay * daysInMonth * monthsInSeason * seasonsInYear) + " ";

		s += (long) timeInSeconds / (secondsInMinute * minutesInHour) % hoursInDay + ":";
		s += (long) timeInSeconds / (secondsInMinute) % minutesInHour + " ";

		s += (long) timeInSeconds % secondsInMinute + ".";
		s += (long) (timeInSeconds * 1000 % 1000) + "";
		return s;
	}

	boolean debug = true;

	public void debug(Object obj)
	{
		if (debug)
			System.out.println(obj);
	}

	public static boolean pathsMatch(Point[] path1, Point[] path2)
	{
		if (path1 == path2)
			return true;
		if (path1 == null || path2 == null)
			return false;
		if (path1.length != path2.length)
			return false;
		for (int i = 0; i < path1.length; i++)
			if (path1[i] == null || path2[i] == null)
				return false;
			else if (path1[i].x != path2[i].x || path1[i].y != path2[i].y)
				return false;
		return true;
	}

	private static final HashMap<String, HashMap<String, ArrayList<String>>> surnames = new HashMap<String, HashMap<String, ArrayList<String>>>();
	private static final HashMap<String, HashMap<String, ArrayList<String>>> names = new HashMap<String, HashMap<String, ArrayList<String>>>();
	// private static final HashMap<String, HashMap<String, ArrayList<String>>>
	// secondNames = new HashMap<String, HashMap<String, ArrayList<String>>>();
	{
		try
		{
			surnames.put("ru", new HashMap<String, ArrayList<String>>());
			surnames.get("ru").put("man", new ArrayList<String>());
			surnames.get("ru").put("woman", new ArrayList<String>());
			names.put("ru", new HashMap<String, ArrayList<String>>());
			names.get("ru").put("man", new ArrayList<String>());
			names.get("ru").put("woman", new ArrayList<String>());
			// secondNames.put("ru", new HashMap<String, ArrayList<String>>());
			// secondNames.get("ru").put("man", new ArrayList<String>());
			// secondNames.get("ru").put("woman", new ArrayList<String>());

			for (String line : Files.readAllLines(Paths.get("E:\\ru.surnames"), Charset.forName("UTF-8")))
				surnames.get("ru").get("man").add(line);
			for (String line : Files.readAllLines(Paths.get("E:\\ru.surnames"), Charset.forName("UTF-8")))
				surnames.get("ru").get("woman").add(line);

			for (String line : Files.readAllLines(Paths.get("E:\\ru.man.names"), Charset.forName("UTF-8")))
				names.get("ru").get("man").add(line);
			for (String line : Files.readAllLines(Paths.get("E:\\ru.woman.names"), Charset.forName("UTF-8")))
				names.get("ru").get("woman").add(line);
			//
			// for (String line : Files.readAllLines(Paths.get("E:\\ru.man.secondnames"),
			// Charset.forName("UTF-8")))
			// secondNames.get("ru").get("man").add(line);
			// for (String line : Files.readAllLines(Paths.get("E:\\ru.woman.secondnames"),
			// Charset.forName("UTF-8")))
			// secondNames.get("ru").get("woman").add(line);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String getSurname(String nationality, Gender gender)
	{
		ArrayList<String> list = surnames.get(nationality).get(gender == Gender.Woman ? "woman" : "man");
		return list.get(random.nextInt(list.size()));
	}

	public static String getName(String nationality, Gender gender)
	{
		ArrayList<String> list = names.get(nationality).get(gender == Gender.Woman ? "woman" : "man");
		return list.get(random.nextInt(list.size()));
	}
	//
	// public static String getSecondName(String nationality, Gender gender)
	// {
	// ArrayList<String> list = secondNames.get(nationality).get(gender ==
	// Gender.Woman ? "woman" : "man");
	// return list.get(random.nextInt(list.size()));
	// }
}
