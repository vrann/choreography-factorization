echo $1
echo $2

scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem $1 ec2-user@54.84.175.140:/home/ec2-user/$2
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem $1 ec2-user@54.147.111.125:/home/ec2-user/$2
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem $1 ec2-user@52.87.201.124:/home/ec2-user/$2
scp -i ~/.ssh/eugene-aws-ec2-nvirginia.pem $1 ec2-user@54.173.109.211:/home/ec2-user/$2
