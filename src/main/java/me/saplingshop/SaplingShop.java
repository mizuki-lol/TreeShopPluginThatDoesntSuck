Run mvn clean package --no-transfer-progress
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------------< me.saplingshop:SaplingShop >---------------------
[INFO] Building SaplingShop 1.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.2.0:clean (default-clean) @ SaplingShop ---
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ SaplingShop ---
[INFO] Artifact javax.inject:javax.inject:jar:1 is present in the local repository, but cached from a remote repository ID that is unavailable in current build context, verifying that is downloadable from [central (https://repo.maven.apache.org/maven2, default, releases)]
[INFO] Artifact javax.inject:javax.inject:jar:1 is present in the local repository, but cached from a remote repository ID that is unavailable in current build context, verifying that is downloadable from [central (https://repo.maven.apache.org/maven2, default, releases)]
[INFO] Copying 1 resource from src/main/resources to target/classes
[INFO] 
[INFO] --- compiler:3.11.0:compile (default-compile) @ SaplingShop ---
[INFO] Changes detected - recompiling the module! :source
target 21] to target/classes
[INFO] Annotation processing is enabled because one or more processors were found
  on the class path. A future release of javac may disable annotation processing
  unless at least one processor is specified by name (-processor), or a search
  path is specified (--processor-path, --processor-module-path), or annotation
  processing is enabled explicitly (-proc:only, -proc:full).
  Use -Xlint:-options to suppress this message.
  Use -proc:none to disable annotation processing.
[INFO] /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/utils/MessageUtil.java: Some input files use or override a deprecated API.
[INFO] /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/utils/MessageUtil.java: Recompile with -Xlint:deprecation for details.
[INFO] -------------------------------------------------------------
Error:  COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/commands/ShopGenCommand.java:[27,15] cannot find symbol
  symbol:   method getGeneratorManager()
  location: variable plugin of type me.saplingshop.SaplingShop
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/listeners/GeneratorListener.java:[48,29] cannot find symbol
  symbol:   method getGeneratorManager()
  location: variable plugin of type me.saplingshop.SaplingShop
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/listeners/GeneratorListener.java:[58,20] cannot find symbol
  symbol:   method getGeneratorManager()
  location: variable plugin of type me.saplingshop.SaplingShop
[INFO] 3 errors 
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  21.744 s
[INFO] Finished at: 2026-06-21T14:51:38Z
[INFO] ------------------------------------------------------------------------
Error:  Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) on project SaplingShop: Compilation failure: Compilation failure: 
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/commands/ShopGenCommand.java:[27,15] cannot find symbol
Error:    symbol:   method getGeneratorManager()
Error:    location: variable plugin of type me.saplingshop.SaplingShop
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/listeners/GeneratorListener.java:[48,29] cannot find symbol
Error:    symbol:   method getGeneratorManager()
Error:    location: variable plugin of type me.saplingshop.SaplingShop
Error:  /home/runner/work/TreeShopPluginThatDoesntSuck/TreeShopPluginThatDoesntSuck/src/main/java/me/saplingshop/listeners/GeneratorListener.java:[58,20] cannot find symbol
Error:    symbol:   method getGeneratorManager()
Error:    location: variable plugin of type me.saplingshop.SaplingShop
Error:  -> [Help 1]
Error:  
Error:  To see the full stack trace of the errors, re-run Maven with the -e switch.
Error:  Re-run Maven using the -X switch to enable full debug logging.
Error:  
Error:  For more information about the errors and possible solutions, please read the following articles:
Error:  [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
Error: Process completed with exit code 1.
