import { memo } from 'react';
import { Handle, Position } from 'reactflow';

interface AIAgentNodeData {
  label: string;
  agentType: string;
  description?: string;
}

interface AIAgentNodeProps {
  data: AIAgentNodeData;
}

const AIAgentNode = ({ data }: AIAgentNodeProps) => {
  const getNodeColor = (type: string) => {
    switch (type) {
      case 'input':
        return '#10b981';
      case 'llm':
        return '#3b82f6';
      case 'tool':
        return '#f59e0b';
      case 'output':
        return '#ef4444';
      case 'condition':
        return '#8b5cf6';
      default:
        return '#6b7280';
    }
  };

  return (
    <div
      style={{
        padding: '15px',
        borderRadius: '8px',
        border: `2px solid ${getNodeColor(data.agentType)}`,
        backgroundColor: '#fff',
        minWidth: '180px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
      }}
    >
      <Handle type="target" position={Position.Top} style={{ background: '#555' }} />

      <div style={{ marginBottom: '8px' }}>
        <div
          style={{
            fontSize: '11px',
            color: getNodeColor(data.agentType),
            fontWeight: 'bold',
            textTransform: 'uppercase',
            marginBottom: '4px',
          }}
        >
          {data.agentType}
        </div>
        <div style={{ fontSize: '14px', fontWeight: '600', color: '#1a192b' }}>
          {data.label}
        </div>
      </div>

      {data.description && (
        <div style={{ fontSize: '12px', color: '#6b7280', marginTop: '8px' }}>
          {data.description}
        </div>
      )}

      <Handle type="source" position={Position.Bottom} style={{ background: '#555' }} />
    </div>
  );
};

export default memo(AIAgentNode);
