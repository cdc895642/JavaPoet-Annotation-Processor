# JavaPoet-Annotation-Processor
В проекте где используется этот процессор нужно описать плагин компиляции следующим образом :
<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>1.8</source>
					<target>1.8</target>
					<annotationProcessors>
						<proc>com.test.annotationprocessor.NewAnnProcessor</proc>
					</annotationProcessors>
				</configuration>
			</plugin>
