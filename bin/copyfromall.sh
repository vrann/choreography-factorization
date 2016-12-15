echo $1
echo $2

scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem  ec2-user@54.84.175.140:/home/ec2-user/$1 $2_1
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem  ec2-user@54.147.111.125:/home/ec2-user/$1 $2_2
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem  ec2-user@52.87.201.124:/home/ec2-user/$1 $2_3
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem  ec2-user@54.173.109.211:/home/ec2-user/$1 $2_4
