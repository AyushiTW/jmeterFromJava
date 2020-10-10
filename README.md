# jmeterFromJava

## run below command on jmeter bin directory:
bin % sh jmeter -n -t /Users/techops/eclipse-workspace/jmeterjava/report/lifeCharger.jmx -l /Users/techops/eclipse-workspace/jmeterjava/output/lifeCharger.csv -e -o /Users/techops/eclipse-workspace/jmeterjava/HTMLreport

note: update the .jmx , .csv , HTMLreport path as per your system

### generate HTML report from csv file on command line 
bin % sh jmeter -g /Users/techops/eclipse-workspace/jmeterjava/output/lifeCharger.csv -o /Users/techops/eclipse-workspace/jmeterjava/HTMLreport