echo $1
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem $1 ec2-user@54.173.109.211:/home/ec2-user/  
