package wcanalysis.heuristic;

import wcanalysis.heuristic.util.Util;

import java.io.File;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGenerator;

/**
 * @author Kasper Luckow
 */
public class PolicyGeneratorListener extends PathListener {

  public static final String UNIFY_POLICIES_CONF = "symbolic.wc.policy.unifypolicies";
  public static final boolean UNIFY_POLICIES_CONF_DEF = false;

  public static final  String VIS_OUTPUT_PATH_CONF = "symbolic.wc.policy.visualizer.outputpath";
  public static final String SER_OUTPUT_PATH_CONF = "symbolic.wc.policy.serializer.outputpath";
  public static final String SERIALIZE_CONF = "symbolic.wc.policy.serialize";
  public static final String SER_OUTPUT_PATH_DEF = "./ser/policy";
  
  //for statistics
  private long newChoices = 0;

  public PolicyGeneratorListener(Config jpfConf, JPF jpf) {
    super(jpfConf, jpf);
  }
  
  @Override
  public void stateAdvanced(Search search) {
    super.stateAdvanced(search);
    ChoiceGenerator<?> cg = search.getVM().getSystemState().getChoiceGenerator();
    if(cg instanceof PCChoiceGenerator) {
      this.newChoices++;
    }
  }
  
  public long getNumberOfNewChoices() {
    return newChoices;
  }

  @Override
  public boolean visualize(Config jpfConf) {
    return jpfConf.hasValue(VIS_OUTPUT_PATH_CONF);
  }

  @Override
  public File getVisualizationDir(Config jpfConf) {
    File out = Util.createDirIfNotExist(jpfConf.getString(VIS_OUTPUT_PATH_CONF, "./vis/policy"));
    return out;
  }

  @Override
  public boolean serialize(Config jpfConf) {
    //Note: we serialize if this configuration is not provided
    return jpfConf.getBoolean(SERIALIZE_CONF, true); 
  }

  @Override
  public boolean unifyPolicies(Config jpfConf) {
    return jpfConf.getBoolean(UNIFY_POLICIES_CONF, UNIFY_POLICIES_CONF_DEF);
  }

  @Override
  public File getPolicyBaseDir(Config jpfConf) {
    File out = Util.createDirIfNotExist(jpfConf.getString(SER_OUTPUT_PATH_CONF, SER_OUTPUT_PATH_DEF));
    return out;
  }
}
