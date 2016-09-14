package wcanalysis.heuristic.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wcanalysis.heuristic.BranchInstruction;
import wcanalysis.heuristic.Path;

/**
 * @author Kasper Luckow
 * Actually not needed atm. Just a wrapper for the storage. lame
 */
public class HistoryBasedBranchPolicy implements BranchPolicy, Unifiable {

  private static final long serialVersionUID = 4478984808375928385L;

  public static class Builder implements BranchPolicyBuilder<HistoryBasedBranchPolicy> {
    private TrieStorage.Builder trieBldr = new TrieStorage.Builder();
    public Builder() { }

    @Override
    public void addPolicy(Path history, int policyChoice) {
      trieBldr.put(history, policyChoice);
    }

    public HistoryBasedBranchPolicy build() {
      return build(false);
    }
    
    public HistoryBasedBranchPolicy build(boolean adaptive) {
      return new HistoryBasedBranchPolicy(trieBldr.build(adaptive));
    }
  }
  
  private BranchPolicyStorage storage;
  
  private HistoryBasedBranchPolicy(BranchPolicyStorage storage) {
    this.storage = storage;
  }
  
  @Override
  public Set<Integer> resolve(Path history) {
    Set<Integer> choices = storage.getChoices(history);
    if(choices != null) {
      return choices;
    }
    return new HashSet<>();
  }

  @Override
  public int getCountsForChoice(int choice) {
    int count = storage.getCountsForChoice(choice);
    return count;
  }
  
  @Override
  public String toString() {
    return storage.toString();
  }

  @Override
  public int getMaxHistorySize() {
    return storage.getMaxHistoryLength();
  }

  //Exposing so much state....
  public BranchPolicyStorage getStorage() {
    return this.storage;
  }

  @Override
  public void unifyWith(BranchPolicy other) throws PolicyUnificationException {
    if(!(other instanceof HistoryBasedBranchPolicy)) {
      throw new PolicyUnificationException("Cannot unify branch policy of type " + other.getClass
          ().getName());
    }

    HistoryBasedBranchPolicy otherBranchPolicy = (HistoryBasedBranchPolicy)other;
    BranchPolicyStorage otherStorage = otherBranchPolicy.getStorage();

    //Merge the two storages---we assume they use a trie backing store
    //This is incredibly ugly
    TrieStorage.Builder trieBldr = new TrieStorage.Builder();
    trieBldr.addStorage((TrieStorage)this.storage);
    trieBldr.addStorage((TrieStorage)otherStorage);

    this.storage = trieBldr.build();
  }
}
