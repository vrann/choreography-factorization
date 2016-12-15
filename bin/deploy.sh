rm -rf deployment
mkdir deployment
cp -rf bin deployment/
cp -rf conf deployment/
cp -rf out/artifacts deployment/
cp -rf ~/.ssh/eugene-aws-ec2-nvirginia.pem deployment
tar -zcf deployment.tar.gz deployment
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem deployment.tar.gz ec2-user@54.173.109.211:/home/ec2-user/  
