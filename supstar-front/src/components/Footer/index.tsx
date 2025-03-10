import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'SupStar Pro',
          title: 'SupStar Pro',
          href: 'https://github.com/hnsqls/supstar',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/hnsqls/supstar',
          blankTarget: true,
        },
        {
          key: 'SupStar',
          title: 'SupStar',
          href: 'https://github.com/hnsqls/supstar',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
