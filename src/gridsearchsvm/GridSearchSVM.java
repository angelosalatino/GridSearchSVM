package gridsearchsvm;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.awt.Color;
import java.lang.Math;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.supportVector.*;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.SelectedTag;

/**
 *
 * @author angelosalatino
 */
public class GridSearchSVM extends javax.swing.JFrame {
    
    /**
     * Creates new form GridSearchSVM
     */
    public GridSearchSVM() {
        initComponents();
        canExecute = true;
    }
    
    /*
    * OWN METHODS
    */
    
    public void openFiles()
    {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(this.inputPath));
            data = new Instances(reader);//oggeto della libreria weka.
            reader.close();
            FileWriter outFile = new FileWriter(this.outputPath, true);
            outf = new PrintWriter(outFile);
            
            this.infoLabelDataset.setText("Information on dataset: "+data.numInstances()+" Instances, "+data.numAttributes()+" Attr.");
            this.textClass.setText(Integer.toString(data.numAttributes()));
            this.textFromFR.setText(Integer.toString(data.numAttributes()-1));
            this.textToFR.setText("1");
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean comboToBoolean(JComboBox comboBoxIn)
    {
        boolean var;
        var = (comboBoxIn.getSelectedItem().toString().equals("True")) ? true : false;
        return var;
    }
    
    public void resetUI()
    {
        this.inputFile.setBorder(null);
        this.textClass.setBorder(null);
        this.textCacheSize.setBorder(null);
        this.textCoef0.setBorder(null);
        this.textDegree.setBorder(null);
        this.textEps.setBorder(null);
        this.textLoss.setBorder(null);
        this.textNu.setBorder(null);
        this.textSeed.setBorder(null);
        this.textNumFolds.setBorder(null);
        this.textGamma.setBorder(null);
        this.textCost.setBorder(null);
        
    }
    
    /**
     * This method takes the values from the GUI and populate the classifier
     */
    public void setClassifier()
    {
        Border border =BorderFactory.createLineBorder(Color.red); //just in case
        
        if(this.inputFile.getText().equals(""))
        {
            this.inputFile.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Select input file!","Error",JOptionPane.ERROR_MESSAGE);
        }
        try{
            data.setClassIndex(Integer.parseInt(this.textClass.getText())-1);
        }catch(NumberFormatException e){
            this.textClass.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Class number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        classifier = new LibSVM();
        
        
        classifier.setSVMType(new SelectedTag(comboSvmType.getSelectedIndex(), LibSVM.TAGS_SVMTYPE));
        
        try{
            classifier.setCacheSize(Double.parseDouble(this.textCacheSize.getText()));
        }catch(NumberFormatException e){
            this.textCacheSize.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Cache size number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        try{
            classifier.setCoef0(Double.parseDouble(this.textCoef0.getText()));
        }catch(NumberFormatException e){
            this.textCoef0.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Coef0 number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        // Cost NOT SETTED FOR NOW
        
        classifier.setDebug(comboToBoolean(this.comboDebug));
        
        try{
            classifier.setDegree(Integer.parseInt(this.textDegree.getText()));
        }catch(NumberFormatException e){
            this.textDegree.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Degree number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        classifier.setDoNotReplaceMissingValues(comboToBoolean(this.comboDoNotReplaceMissingValues));
        
        try{
            classifier.setEps(Double.parseDouble(this.textEps.getText()));
        }catch(NumberFormatException e){
            this.textEps.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Eps number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        // Gamma NOT SETTED FOR NOW
        
        classifier.setKernelType(new SelectedTag(comboKernelType.getSelectedIndex(),LibSVM.TAGS_SVMTYPE));
        
        try{
            classifier.setLoss(Double.parseDouble(this.textLoss.getText()));
        }catch(NumberFormatException e){
            this.textLoss.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Loss number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        classifier.setNormalize(comboToBoolean(this.comboNormalize));
        
        try{
            classifier.setNu(Double.parseDouble(this.textNu.getText()));
        }catch(NumberFormatException e){
            this.textNu.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Nu number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        classifier.setProbabilityEstimates(comboToBoolean(this.comboProbabilityEstimates));
        
        try{
            seed = Integer.parseInt(this.textSeed.getText());
        }catch(NumberFormatException e){
            this.textSeed.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Seed number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        classifier.setShrinking(comboToBoolean(this.comboShrinking));
        
        try{
            folds = Integer.parseInt(this.textNumFolds.getText());
        }catch(NumberFormatException e){
            this.textNumFolds.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Fold number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        try{
            gammaStep = Double.parseDouble(this.textGamma.getText());
        }catch(NumberFormatException e){
            this.textGamma.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Gamma step number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        try{
            costStep = Double.parseDouble(this.textCost.getText());
        }catch(NumberFormatException e){
            this.textCost.setBorder(border);
            canExecute = false;
            JOptionPane.showMessageDialog(this, "Cost step number not valid!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    public void checkFeatureReduction()
    {
        //it is supposed that the check box is checked, but it is necessary another control
        Border border =BorderFactory.createLineBorder(Color.red);
        if(this.checkBoxFeatures.isSelected())
        {
            try{
                featFrom = Integer.parseInt(this.textFromFR.getText());
            }catch(NumberFormatException e){
                this.textFromFR.setBorder(border);
                canExecute = false;
                JOptionPane.showMessageDialog(this, "From Feature number not valid!","Error",JOptionPane.ERROR_MESSAGE);
            }
            try{
                featTo = Integer.parseInt(this.textToFR.getText());
            }catch(NumberFormatException e){
                this.textToFR.setBorder(border);
                canExecute = false;
                JOptionPane.showMessageDialog(this, "To Feature number not valid!","Error",JOptionPane.ERROR_MESSAGE);
            }
            
            if (featTo >= featFrom)
            {
                this.textFromFR.setBorder(border);
                this.textToFR.setBorder(border);
                canExecute = false;
                JOptionPane.showMessageDialog(this, "To Feature number is greater than From Feature number!","Error",JOptionPane.ERROR_MESSAGE);
            }
            
            if (featFrom > data.numAttributes())
            {
                this.textFromFR.setBorder(border);
                canExecute = false;
                JOptionPane.showMessageDialog(this, "From Feature number is greater than number of attributes!","Error",JOptionPane.ERROR_MESSAGE);
            }
            
            if (featTo < 0)
            {
                this.textToFR.setBorder(border);
                canExecute = false;
                JOptionPane.showMessageDialog(this, "To Feature number is less than 0!","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else
        {
            JOptionPane.showMessageDialog(this, "Error 1. Something is gone wrong","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * This method sets the values of cost and gamma for the i-th cicle of the grid search
     * @param cost The cost parameter
     * @param gamma The gamma parameter
     */
    public void setCostAndGamma(double cost, double gamma)
    {
        classifier.setCost(Math.pow(2, cost));
        classifier.setGamma(Math.pow(2, gamma));
    }
    
    /**
     * Execute the grid search
     */
    public void executeGridSearch()
    {
        /*
        *  gamma = Math.pow(2, powgamma); // da 2^-15 a 2^3 ... 10 cicli
        * cost = Math.pow(2, powcost);// da 2^-5 a 2^15 ... 11 cicli
        */
        System.out.println("avvio thread");
        new Thread(new Runnable() {
            
            
            public void run() {
                System.out.println("thread avviato");
                
                try{
                    
                    Evaluation eval = new Evaluation(data);//qui indico le features
                    
                    int numberOfCycle = (int)java.lang.Math.ceil((19/gammaStep))*(int)java.lang.Math.ceil((21/costStep));
                    double step = 100/(double)numberOfCycle;
                    double progress = 0;
                    for (int k = featFrom; k > featTo; k--)
                    {
                        for (double i = -5; i <= 15; i = i + costStep)
                        {
                            for (double j = -15; j <= 3; j = j + gammaStep)
                            {
                                
                                
                                //this.runProgressBar.setStringPainted(true);
                                //this.labelProgressBar.setText("Completed "+Double.toString(java.lang.Math.ceil(progress))+"%");
                                setCostAndGamma(i,j);
                                Classifier classCopy = Classifier.makeCopy(classifier);
                                classCopy.buildClassifier(data);
                                eval.crossValidateModel(classCopy, data, folds, new Random(seed), new Object[] { });
                                outf.println("Cost: "+i+" Gamma: "+j+" Attributi: " + data.numAttributes()+" F Measure: "+eval.weightedFMeasure() + " True Positive: " + eval.weightedTruePositiveRate());
                                outf.flush();
                                progress += step;
                                runProgressBar.setValue((int)java.lang.Math.ceil(progress));
                                runProgressBar.repaint();
                            }
                        }
                        if(data.numAttributes() >= 2) data.deleteAttributeAt(data.numAttributes() - 2);
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        
        
    }
    
    /**
     * Execute the grid search reducing the number of variable
     */
    public void executeGridSearchWithVariableReducing()
    {
        System.out.println("avvio thread");
//        new Thread(new Runnable() {
//            
//            
//            public void run() {
//                System.out.println("thread avviato");
//                try{
//                    
//                    Evaluation eval = new Evaluation(data);//qui indico le features
//                    
//                    int numberOfCycle = (int)java.lang.Math.ceil((19/gammaStep))*(int)java.lang.Math.ceil((21/costStep))*(featFrom - featTo);
//                    double step = 100/(double)numberOfCycle;
//                    double progress = 0;
//                    
//                    
//                    for (double i = -5; i <= 15; i = i + costStep)
//                    {
//                        for (double j = -15; j <= 3; j = j + gammaStep)
//                        {
//                            
//                            
//                            //this.runProgressBar.setStringPainted(true);
//                            //this.labelProgressBar.setText("Completed "+Double.toString(java.lang.Math.ceil(progress))+"%");
//                            setCostAndGamma(i,j);
//                            Classifier classCopy = Classifier.makeCopy(classifier);
//                            classCopy.buildClassifier(data);
//                            eval.crossValidateModel(classCopy, data, folds, new Random(seed), new Object[] { });
//                            outf.println("Cost: "+i+" Gamma: "+j+" Attributi: " + data.numAttributes()+" F Measure: "+eval.weightedFMeasure() + " True Positive: " + eval.weightedTruePositiveRate());
//                            outf.flush();
//                            progress += step;
//                            runProgressBar.setValue((int)java.lang.Math.ceil(progress));
//                            runProgressBar.repaint();
//                        }
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        try{
                    
                    Evaluation eval = new Evaluation(data);//qui indico le features
                    
                    int numberOfCycle = (int)java.lang.Math.ceil((19/gammaStep))*(int)java.lang.Math.ceil((21/costStep))*(featFrom - featTo);
                    double step = 100/(double)numberOfCycle;
                    double progress = 0;
                    
                    
                    for (double i = -5; i <= 15; i = i + costStep)
                    {
                        for (double j = -15; j <= 3; j = j + gammaStep)
                        {
                            
                            
                            //this.runProgressBar.setStringPainted(true);
                            //this.labelProgressBar.setText("Completed "+Double.toString(java.lang.Math.ceil(progress))+"%");
                            setCostAndGamma(i,j);
                            Classifier classCopy = Classifier.makeCopy(classifier);
                            classCopy.buildClassifier(data);
                            eval.crossValidateModel(classCopy, data, folds, new Random(seed), new Object[] { });
                            outf.println("Cost: "+i+" Gamma: "+j+" Attributi: " + data.numAttributes()+" F Measure: "+eval.weightedFMeasure() + " True Positive: " + eval.weightedTruePositiveRate());
                            outf.flush();
                            progress += step;
                            runProgressBar.setValue((int)java.lang.Math.ceil(progress));
                            runProgressBar.repaint();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        runButton = new javax.swing.JButton();
        runProgressBar = new javax.swing.JProgressBar();
        inputFile = new javax.swing.JTextField();
        selectFile = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        checkBoxFeatures = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        textFromFR = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textToFR = new javax.swing.JTextField();
        infoLabelDataset = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        textClass = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        comboSvmType = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        textCacheSize = new javax.swing.JTextField();
        textCoef0 = new javax.swing.JTextField();
        textCost = new javax.swing.JTextField();
        comboDebug = new javax.swing.JComboBox();
        textDegree = new javax.swing.JTextField();
        comboDoNotReplaceMissingValues = new javax.swing.JComboBox();
        textEps = new javax.swing.JTextField();
        textGamma = new javax.swing.JTextField();
        comboKernelType = new javax.swing.JComboBox();
        textLoss = new javax.swing.JTextField();
        comboNormalize = new javax.swing.JComboBox();
        textNu = new javax.swing.JTextField();
        comboProbabilityEstimates = new javax.swing.JComboBox();
        textSeed = new javax.swing.JTextField();
        comboShrinking = new javax.swing.JComboBox();
        textWeights = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        textNumFolds = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        costStepLabelHelp = new javax.swing.JLabel();
        gammaStepLabelHelp = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        selectFile.setText("Select file");
        selectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileActionPerformed(evt);
            }
        });

        jLabel1.setText("Input File");

        jLabel2.setText("SVM Classifier ");

        jLabel3.setText("Run Section");

        checkBoxFeatures.setText("Feature reduction");

        jLabel4.setText("From:");

        jLabel5.setText("To:");

        infoLabelDataset.setText("Information on dataset:");

        jLabel7.setText("Class:");

        jLabel8.setText("SVMType");

        comboSvmType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C-SVC (classification)", "nu-SVC (classification)", "one-class SVM (classification)", "epsilon-SVR (regression)", "nu-SVR (regression)" }));

        jLabel9.setText("cacheSize");

        textCacheSize.setText("40.0");

        textCoef0.setText("0.0");

        textCost.setText("2");

        comboDebug.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "True", "False" }));
        comboDebug.setSelectedIndex(1);

        textDegree.setText("3");

        comboDoNotReplaceMissingValues.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "True", "False" }));
        comboDoNotReplaceMissingValues.setSelectedIndex(1);

        textEps.setText("0.001");

        textGamma.setText("2");

        comboKernelType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "linear: u'*v", "polynomial: (gamma*u'*v + coef0)^degree", "radial basis fuction: exp(-gamma*|u-v|^2)", "sigmoid: tanh(gamma*u'*v + coef0)" }));
        comboKernelType.setSelectedIndex(2);

        textLoss.setText("0.1");

        comboNormalize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "True", "False" }));
        comboNormalize.setSelectedIndex(1);

        textNu.setText("0.5");

        comboProbabilityEstimates.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "False", "True" }));

        textSeed.setText("1");

        comboShrinking.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "True", "False" }));

        jLabel10.setText("coef0");

        jLabel11.setText("cost");

        jLabel12.setText("debug");

        jLabel13.setText("degree");

        jLabel14.setText("doNotReplaceMissingValues");

        jLabel15.setText("eps");

        jLabel16.setText("gamma");

        jLabel17.setText("kernelType");

        jLabel18.setText("loss");

        jLabel19.setText("normalize");

        jLabel20.setText("nu");

        jLabel21.setText("probabilityEstimates");

        jLabel22.setText("seed");

        jLabel23.setText("shrinking");

        jLabel24.setText("weights");

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel6.setText("Cross Validation");

        jLabel25.setText("NumFolds:");

        textNumFolds.setText("10");

        jLabel26.setText("[2^-5 ... 2^15],                 costStep");

        jLabel27.setText("[2^-15 ... 2^3],           costGamma");

        costStepLabelHelp.setText("?");

        gammaStepLabelHelp.setText("?");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel25)
                                .addGap(2, 2, 2)
                                .addComponent(textNumFolds, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(textFromFR, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(textToFR, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(checkBoxFeatures))
                        .addGap(48, 48, 48))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(runButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel24))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textGamma))
                                    .addComponent(comboKernelType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textCacheSize)
                                    .addComponent(textCoef0)
                                    .addComponent(comboSvmType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(comboDebug, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textDegree)
                                    .addComponent(comboDoNotReplaceMissingValues, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textLoss)
                                    .addComponent(textEps)
                                    .addComponent(comboNormalize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textNu)
                                    .addComponent(comboProbabilityEstimates, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textSeed)
                                    .addComponent(comboShrinking, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textWeights)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textCost)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(gammaStepLabelHelp)
                                    .addComponent(costStepLabelHelp))
                                .addGap(15, 15, 15)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(runProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(inputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(selectFile))
                                            .addComponent(jLabel2))
                                        .addGap(42, 42, 42))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(infoLabelDataset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addComponent(textClass, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(inputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFile))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(infoLabelDataset)
                    .addComponent(jLabel7)
                    .addComponent(textClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboSvmType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textCacheSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textCoef0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel11)
                    .addComponent(costStepLabelHelp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboDebug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textDegree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboDoNotReplaceMissingValues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textEps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textGamma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(gammaStepLabelHelp)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboKernelType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textLoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboNormalize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textNu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboProbabilityEstimates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboShrinking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textWeights, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(checkBoxFeatures)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(textFromFR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(textToFR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(textNumFolds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(runButton))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(runProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        // TODO add your handling code here:
        canExecute = true;
        this.resetUI();
        this.setClassifier();
        
        
        if(this.checkBoxFeatures.isSelected())
        {
            this.checkFeatureReduction();
            if (canExecute) this.executeGridSearchWithVariableReducing();
        }else
        {
            if (canExecute) this.executeGridSearch();
        }
        
    }//GEN-LAST:event_runButtonActionPerformed
    
    private void selectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            
            public String getDescription() {
                return "Arff Documents (*.arff)";
            }
            
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".arff");
                }
            }
        });
        int status = fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            //checking the extension
            String extension = "";
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i+1);
            }
            if( !extension.toLowerCase().equals("arff") )
            {
                JOptionPane.showMessageDialog(new JFrame(), "Err 1: File extension is not Arff. Reload a correct file.", "Dialog",JOptionPane.ERROR_MESSAGE);
            }else
            {
                this.inputFile.setText(selectedFile.getAbsolutePath());
                this.inputPath = selectedFile.getAbsolutePath();
                
                this.outputPath = fileName.substring(0, i) + ".txt"; //the output file is a txt file
                
                this.openFiles();
                
            }
            //System.out.println(selectedFile.getParent());
            //System.out.println(selectedFile.getName());
            //System.out.println(outputPath);
        } 
    }//GEN-LAST:event_selectFileActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
        */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GridSearchSVM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GridSearchSVM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GridSearchSVM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GridSearchSVM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GridSearchSVM().setVisible(true);
            }
        });
    }
    
    // Other variables
    String inputPath;
    String outputPath;
    Instances data;
    LibSVM classifier;
    
    int seed; //for random numbers
    double gammaStep;
    double costStep;
    int folds; //for cross validation
    
    boolean canExecute;
    
    int featFrom;
    int featTo;
    
    PrintWriter outf;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxFeatures;
    private javax.swing.JComboBox comboDebug;
    private javax.swing.JComboBox comboDoNotReplaceMissingValues;
    private javax.swing.JComboBox comboKernelType;
    private javax.swing.JComboBox comboNormalize;
    private javax.swing.JComboBox comboProbabilityEstimates;
    private javax.swing.JComboBox comboShrinking;
    private javax.swing.JComboBox comboSvmType;
    private javax.swing.JLabel costStepLabelHelp;
    private javax.swing.JLabel gammaStepLabelHelp;
    private javax.swing.JLabel infoLabelDataset;
    private javax.swing.JTextField inputFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton runButton;
    private javax.swing.JProgressBar runProgressBar;
    private javax.swing.JButton selectFile;
    private javax.swing.JTextField textCacheSize;
    private javax.swing.JTextField textClass;
    private javax.swing.JTextField textCoef0;
    private javax.swing.JTextField textCost;
    private javax.swing.JTextField textDegree;
    private javax.swing.JTextField textEps;
    private javax.swing.JTextField textFromFR;
    private javax.swing.JTextField textGamma;
    private javax.swing.JTextField textLoss;
    private javax.swing.JTextField textNu;
    private javax.swing.JTextField textNumFolds;
    private javax.swing.JTextField textSeed;
    private javax.swing.JTextField textToFR;
    private javax.swing.JTextField textWeights;
    // End of variables declaration//GEN-END:variables
}
