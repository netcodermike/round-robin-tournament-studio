import java.awt.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Tournament {
    record Match(int round,String home,String away){}
    record Score(int home,int away){}
    static class Standing {String name;int played,points,forGoals,against;Standing(String n){name=n;}int difference(){return forGoals-against;}}
    static List<Match> schedule(List<String> names){
        if(names.size()<2||names.size()>32||new HashSet<>(names).size()!=names.size()||names.stream().anyMatch(String::isBlank))throw new IllegalArgumentException("Use 2–32 unique nonempty names");
        List<String> rotation=new ArrayList<>(names);if(rotation.size()%2==1)rotation.add(null);List<Match> out=new ArrayList<>();int n=rotation.size();
        for(int round=1;round<n;round++){
            for(int i=0;i<n/2;i++){String a=rotation.get(i),b=rotation.get(n-1-i);if(a!=null&&b!=null)out.add(round%2==1?new Match(round,a,b):new Match(round,b,a));}
            rotation.add(1,rotation.remove(n-1));
        }return out;
    }
    static List<Standing> standings(List<String> names,List<Match> matches,Map<Integer,Score> results){
        Map<String,Standing> table=new HashMap<>();for(String n:names)table.put(n,new Standing(n));
        for(var entry:results.entrySet()){
            if(entry.getKey()<0||entry.getKey()>=matches.size())throw new IllegalArgumentException("Invalid match index");Score s=entry.getValue();if(s.home<0||s.away<0||s.home>999||s.away>999)throw new IllegalArgumentException("Scores must be 0–999");
            Match m=matches.get(entry.getKey());Standing h=table.get(m.home),a=table.get(m.away);h.played++;a.played++;h.forGoals+=s.home;h.against+=s.away;a.forGoals+=s.away;a.against+=s.home;
            if(s.home>s.away)h.points+=3;else if(s.home<s.away)a.points+=3;else{h.points++;a.points++;}
        }
        List<Standing> sorted=new ArrayList<>(table.values());sorted.sort(Comparator.comparingInt((Standing s)->s.points).reversed().thenComparing(Comparator.comparingInt(Standing::difference).reversed()).thenComparing(Comparator.comparingInt((Standing s)->s.forGoals).reversed()).thenComparing(s->s.name));return sorted;
    }
    static String render(List<String> names,List<Match> games,Map<Integer,Score> results){
        StringBuilder out=new StringBuilder("STANDINGS\nTeam                     P   Pts    GF    GA    GD\n");
        for(Standing s:standings(names,games,results))out.append(String.format("%-24s %2d %5d %5d %5d %5d%n",s.name,s.played,s.points,s.forGoals,s.against,s.difference()));
        out.append("\nFIXTURES\n");for(int i=0;i<games.size();i++){Match m=games.get(i);Score s=results.get(i);out.append(i+1).append(". R").append(m.round).append(" ").append(m.home).append(" vs ").append(m.away).append(s==null?" [unplayed]":" "+s.home+"–"+s.away).append('\n');}return out.toString();
    }
    static void save(Path path,List<String> names,Map<Integer,Score> scores)throws IOException{
        Properties p=new Properties();p.setProperty("teams",Integer.toString(names.size()));for(int i=0;i<names.size();i++)p.setProperty("team."+i,names.get(i));for(var e:scores.entrySet())p.setProperty("score."+e.getKey(),e.getValue().home+","+e.getValue().away);
        Path full=path.toAbsolutePath();Files.createDirectories(full.getParent());Path temp=Files.createTempFile(full.getParent(),"tournament-",".tmp");try{try(Writer w=Files.newBufferedWriter(temp)){p.store(w,"Tournament Studio");}Files.move(temp,full,StandardCopyOption.REPLACE_EXISTING);}finally{Files.deleteIfExists(temp);}
    }
    static void selfTest()throws Exception{
        for(int n=2;n<=9;n++){List<String> names=new ArrayList<>();for(int i=0;i<n;i++)names.add("Team "+i);var matches=schedule(names);if(matches.size()!=n*(n-1)/2)throw new AssertionError("wrong game count");Set<Set<String>> pairs=new HashSet<>();for(Match m:matches)if(!pairs.add(Set.of(m.home,m.away)))throw new AssertionError("duplicate pairing");for(int round=1;round<=n;round++){Set<String> played=new HashSet<>();for(Match m:matches)if(m.round==round&&(!played.add(m.home)||!played.add(m.away)))throw new AssertionError("team plays twice");}}
        var table=standings(List.of("A","B"),schedule(List.of("A","B")),Map.of(0,new Score(2,1)));if(!table.get(0).name.equals("A")||table.get(0).points!=3)throw new AssertionError();System.out.println("Pairing, round uniqueness, and standings passed");
    }
    static class Studio {
        List<String> names=List.of("Falcons","Otters","Foxes","Owls");List<Match> games=schedule(names);Map<Integer,Score> scores=new HashMap<>();
        final JFrame frame=new JFrame("Round-Robin Tournament Studio");final JTextArea output=new JTextArea();final JComboBox<String> picker=new JComboBox<>();final Path path;
        Studio(Path p)throws Exception{path=p;if(Files.exists(path)){Properties data=new Properties();try(Reader r=Files.newBufferedReader(path)){data.load(r);}int count=Integer.parseInt(data.getProperty("teams"));if(count<2||count>32)throw new IllegalArgumentException("Invalid saved team count");List<String> loaded=new ArrayList<>();for(int i=0;i<count;i++)loaded.add(Objects.requireNonNull(data.getProperty("team."+i)));names=loaded;games=schedule(names);for(String k:data.stringPropertyNames())if(k.startsWith("score.")){String[] v=data.getProperty(k).split(",");scores.put(Integer.parseInt(k.substring(6)),new Score(Integer.parseInt(v[0]),Integer.parseInt(v[1])));}standings(names,games,scores);}}
        void refresh(){picker.removeAllItems();for(int i=0;i<games.size();i++){Match m=games.get(i);picker.addItem((i+1)+". "+m.home+" vs "+m.away);}output.setText(render(names,games,scores));}
        void persist(){try{save(path,names,scores);}catch(IOException e){JOptionPane.showMessageDialog(frame,"Save failed: "+e.getMessage());}}
        void show(){
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);frame.setSize(900,680);JPanel root=new JPanel(new BorderLayout(12,12));root.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));frame.setContentPane(root);output.setEditable(false);output.setFont(new Font("Monospaced",0,14));root.add(new JScrollPane(output),BorderLayout.CENTER);
            JPanel controls=new JPanel();JTextField home=new JTextField("0",3),away=new JTextField("0",3);controls.add(picker);controls.add(home);controls.add(new JLabel(":"));controls.add(away);JButton result=new JButton("Record score");controls.add(result);root.add(controls,BorderLayout.SOUTH);
            result.addActionListener(e->{try{int h=Integer.parseInt(home.getText()),a=Integer.parseInt(away.getText());if(h<0||a<0||h>999||a>999)throw new IllegalArgumentException("Scores must be 0–999");scores.put(picker.getSelectedIndex(),new Score(h,a));persist();int index=picker.getSelectedIndex();refresh();picker.setSelectedIndex(index);}catch(RuntimeException error){JOptionPane.showMessageDialog(frame,error.getMessage());}});
            JPanel top=new JPanel();JButton create=new JButton("New tournament"),export=new JButton("Export text");top.add(create);top.add(export);root.add(top,BorderLayout.NORTH);
            create.addActionListener(e->{String raw=JOptionPane.showInputDialog(frame,"Comma-separated team names (replaces current tournament):");if(raw==null)return;try{List<String> next=Arrays.stream(raw.split(",",-1)).map(String::trim).toList();var nextGames=schedule(next);names=next;games=nextGames;scores.clear();persist();refresh();}catch(RuntimeException error){JOptionPane.showMessageDialog(frame,error.getMessage());}});
            export.addActionListener(e->{JFileChooser chooser=new JFileChooser();if(chooser.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)try{Files.writeString(chooser.getSelectedFile().toPath(),output.getText());}catch(IOException error){JOptionPane.showMessageDialog(frame,error.getMessage());}});refresh();frame.setLocationRelativeTo(null);frame.setVisible(true);
        }
    }
    public static void main(String[]args)throws Exception{if(args.length>0&&args[0].equals("--self-test")){selfTest();return;}SwingUtilities.invokeLater(()->{try{new Studio(Path.of(args.length>0?args[0]:"tournament.properties")).show();}catch(Exception e){JOptionPane.showMessageDialog(null,"Cannot load tournament: "+e.getMessage());}});}
}
