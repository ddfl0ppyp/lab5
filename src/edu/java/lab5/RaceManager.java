package edu.java.lab5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class RaceManager {

	private JFrame raceList;
	private DefaultTableModel model;
	private JButton save;
	private JButton open;
	private JButton add;
	private JButton edit;
	private JButton delete;
	private JButton pedestal;
	private JToolBar toolBar;
	private JScrollPane scroll;
	private JTable race;
	private JComboBox team;
	private JTextField driver;
	private JButton filter;
    private Set<String> teams = new HashSet<>();
    private Set<String> drivers = new HashSet<>();
    private Set<String> tracks = new HashSet<>();
    String[] tmpStrings;
	int selectedRow = -1;

	public void show() 
	{
		raceList = new JFrame("Список гонок");
		raceList.setSize(800, 500);
		raceList.setLocation(100, 100);
		raceList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		save = new JButton(new ImageIcon("./img/diskette.png"));
		open = new JButton(new ImageIcon("./img/open-file.png"));
		add = new JButton(new ImageIcon("./img/add (1).png"));
		edit = new JButton(new ImageIcon("./img/edit (1).png"));
		delete = new JButton(new ImageIcon("./img/minus.png"));
		pedestal = new JButton(new ImageIcon("./img/pedestal.png"));
		
		save.setToolTipText("Сохранить список");
		open.setToolTipText("Открыть список");
		add.setToolTipText("Добавить запись");
		edit.setToolTipText("Изменить запись");
		delete.setToolTipText("Удалить запись");
		pedestal.setToolTipText("Показать призеров");

		toolBar = new JToolBar("Панель инструментов");
		toolBar.add(add);
		toolBar.add(edit);
		toolBar.add(delete);
		toolBar.add(save);
		toolBar.add(open);
		toolBar.add(pedestal);
		
		raceList.setLayout(new BorderLayout());
		raceList.add(toolBar, BorderLayout.NORTH);
		
		String [] columnsRace = {"Команда","Пилот","Трасса","Дата и время","Место","Очки"};
        model = new DefaultTableModel(null, columnsRace);
        race = new JTable(model);
        race.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scroll = new JScrollPane(race);
        raceList.add(scroll, BorderLayout.CENTER);
        
        
        team = new JComboBox(new String[]{"Команда"}); 
        driver = new JTextField("Имя пилота");
        driver.setPreferredSize(new Dimension(200, 30));
        filter = new JButton("Поиск");
        JPanel filterPanel = new JPanel();
        filterPanel.add(team);
        filterPanel.add(driver);
        filterPanel.add(filter);
        
        raceList.add(filterPanel, BorderLayout.SOUTH);
        
        race.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
        {
            public void valueChanged(ListSelectionEvent e) 
            {
                if (!e.getValueIsAdjusting()) selectedRow = race.getSelectedRow();
            }
        });
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                FileDialog save = new FileDialog(raceList, "Сохранить файл", FileDialog.SAVE);
                save.setFile(".csv");
                save.setVisible(true);
                String fileName = save.getDirectory() + save.getFile();
                if(fileName.equals("nullnull")) return;
                else
                try
                {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                    for(int i=0;i<model.getRowCount();i++)
                    {
                        for(int j=0;j<model.getColumnCount();j++)
                        {
                            writer.write((String)model.getValueAt(i,j));
                            if(j!=model.getColumnCount()-1) writer.write(",");
                        }
                        writer.write("\n");
                    }
                    writer.close();
                }
                catch(IOException IOEx) {IOEx.printStackTrace();}
            }
        });
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                FileDialog load = new FileDialog(raceList, "Открыть файл", FileDialog.LOAD);
                load.setFile(".csv");
                load.setVisible(true);
                String fileName = load.getDirectory() + load.getFile();
                if(fileName == null) return;
                try
                {
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
                    int rows = model.getRowCount();
                    for(int i=0;i<rows;i++) model.removeRow(0);
                    String line;
                    do
                    {
                        line = reader.readLine();
                        if(line != null)
                        {
                            model.addRow(line.split(","));
                            teams.add(line.split(",")[0]);
                            drivers.add(line.split(",")[1]);
                            tracks.add(line.split(",")[2]);
                        }
                    } while(line != null);
                    tmpStrings = new String[teams.size()+1];
                    tmpStrings[0]="Команда";if(teams.size() > 0) System.arraycopy(teams.toArray(new String[0]), 0, tmpStrings, 1, teams.size()-1);
                    team = new JComboBox(tmpStrings); 
                    reader.close();
                }
                catch(FileNotFoundException FNFEx) {FNFEx.printStackTrace();}
                catch(IOException IOEx) {IOEx.printStackTrace();}
            }
        });
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                rowEditor();
            }
        });
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if(selectedRow==-1) throw new NoRowException();

                }
                catch(NoRowException ex) { JOptionPane.showMessageDialog(raceList, ex.getMessage());}
            }
        });
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if(selectedRow==-1) throw new NoRowException();
                    model.removeRow(selectedRow);
                }
                catch(NoRowException ex) { JOptionPane.showMessageDialog(raceList, ex.getMessage());}
            }
        });
        pedestal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if(selectedRow==-1) throw new NoRowException();
                    JOptionPane.showMessageDialog(raceList, "Тройка лучших", null, 1);
                }
                catch(NoRowException ex) { JOptionPane.showMessageDialog(raceList, ex.getMessage());}
            }
        });
        driver.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                    driver.selectAll();
            }
            public void focusLost(FocusEvent e) {
                if (driver.getText().isEmpty()) {
                    driver.setText("Имя пилота");
                }
            }
        });
        filter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    checkName(driver);
                    String name = driver.getText();
                    Object teamName = team.getSelectedItem();
                    for(int row=0;row<model.getRowCount();) 
                    {
                        if( (name.equals("*") || ((String)model.getValueAt(row,1)).equals(name)) && (teamName.equals("Команда") || ((String)model.getValueAt(row,0)).equals(teamName)) ) row++;
                        else model.removeRow(row);
                    }
                }
                catch(NullPointerException ex) { JOptionPane.showMessageDialog(raceList, ex.toString());}
                catch(NoNameException noNameEx) { JOptionPane.showMessageDialog(raceList, noNameEx.getMessage());}
            }
        });

        raceList.setVisible(true);
		}
		
		public static void main(String[] args) {
			new RaceManager().show();
		}
		
		private class NoRowException extends Exception
		{
			/**
			 * Исключение вызывается при попытке получения тройки лучших пилотов, 
			 * если не выбрана ни одна строка таблицы
			 */
			public NoRowException()
			{
				super("Не выбрано ни одной строки!");
			}
		}

		private class NoNameException extends Exception 
		{
			/**
			 * Исключение вызывается при попытке поиска, если поле поиска не изменено
			 */
			public NoNameException() 
			{
				super ("Вы не ввели имя для поиска!");
			}
		}

		private void checkName (JTextField bName) throws NoNameException,NullPointerException
		{
			String sName = bName.getText();
			if (sName.contains("Имя пилота")) throw new NoNameException();
			if (sName.length() == 0) throw new NullPointerException();
		}  
	
		private void rowEditor()
        {
            JDialog inputDialog = new JDialog();
            inputDialog.setSize(300, 300);
            inputDialog.setLayout(new GridLayout(0, 2));

            JLabel driverLabel = new JLabel("Имя");
            tmpStrings = new String[drivers.size()+1];
            tmpStrings[0]="";if(drivers.size() > 0) System.arraycopy(drivers.toArray(new String[0]), 0, tmpStrings, 1, drivers.size());
            JComboBox driverName = new JComboBox<>(tmpStrings);
            driverName.setEditable(true);

            JLabel teamLabel = new JLabel("Команда");
            tmpStrings = new String[teams.size()+1];
            tmpStrings[0]="";if(teams.size() > 0) System.arraycopy(teams.toArray(new String[0]), 0, tmpStrings, 1, teams.size());
            JComboBox teamName = new JComboBox<>(tmpStrings);
            teamName.setEditable(true);

            JLabel trackLabel = new JLabel("Трасса");
            tmpStrings = new String[tracks.size()+1];
            tmpStrings[0]="";if(tracks.size() > 0) System.arraycopy(tracks.toArray(new String[0]), 0, tmpStrings, 1, tracks.size());
            JComboBox trackName = new JComboBox<>(tmpStrings);
            trackName.setEditable(true);

            JLabel dateLabel = new JLabel("Дата и время");
            JTextField dateField = new JTextField(20);

            JLabel placeLabel = new JLabel("Место");
            JTextField placeField = new JTextField(20);

            JLabel pointsLabel = new JLabel("Очки");
            JTextField pointsField = new JTextField(20);

            JButton submitButton = new JButton("Готово");

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    model.addRow(new String[]{(String)teamName.getSelectedItem(),(String)driverName.getSelectedItem(),(String)trackName.getSelectedItem(),dateField.getText(),placeField.getText(),pointsField.getText()});
                    inputDialog.dispose();
                }
            });
    
            inputDialog.add(teamLabel);
            inputDialog.add(teamName);
            inputDialog.add(driverLabel);
            inputDialog.add(driverName);
            inputDialog.add(trackLabel);
            inputDialog.add(trackName);
            inputDialog.add(dateLabel);
            inputDialog.add(dateField);
            inputDialog.add(placeLabel);
            inputDialog.add(placeField);
            inputDialog.add(pointsLabel);
            inputDialog.add(pointsField);
            inputDialog.add(submitButton);
    
            inputDialog.setVisible(true);
        }
	}
	